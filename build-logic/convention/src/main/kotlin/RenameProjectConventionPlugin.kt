import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Registers the one-time `renameProject` task on the root project. It turns a fresh copy of the
 * template into a new app by replacing the template package, project name, and app name.
 *
 * Usage: `./gradlew renameProject -PnewPackage=com.mycompany.mynewapp -PnewProjectName=MyNewApp`
 * (optional: `-PnewAppName="My New App"`, defaults to the project name split into words).
 */
class RenameProjectConventionPlugin : Plugin<Project> {

    /** Adds the `renameProject` task to [target] and wires its inputs to Gradle properties. */
    override fun apply(target: Project) {
        target.tasks.register("renameProject", RenameProjectTask::class.java) {
            group = "setup"
            description = "Renames the template package, project name, and app name (run once)."
            projectDir.set(target.layout.projectDirectory)
            newPackage.set(target.providers.gradleProperty("newPackage"))
            newProjectName.set(target.providers.gradleProperty("newProjectName"))
            newAppName.set(target.providers.gradleProperty("newAppName"))
        }
    }
}

/**
 * Rewrites the project in place: replaces the template package in sources, build files, and the
 * iOS configuration (bundle id), moves the source folders of every source set to the new package
 * path, updates `rootProject.name`, and replaces the app name in the Android and Compose string
 * resources, the iOS configuration, and the web page title.
 * `build-logic` is left untouched because it contains no app code.
 */
@DisableCachingByDefault(because = "Rewrites the project's own source files")
abstract class RenameProjectTask : DefaultTask() {

    @get:Internal
    abstract val projectDir: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val newPackage: Property<String>

    @get:Input
    @get:Optional
    abstract val newProjectName: Property<String>

    @get:Input
    @get:Optional
    abstract val newAppName: Property<String>

    /** Validates the inputs, then renames the package, the source folders, and the names. */
    @TaskAction
    fun rename() {
        val root = projectDir.get().asFile
        val pkg = newPackage.orNull ?: throw GradleException(USAGE)
        val projectName = newProjectName.orNull ?: throw GradleException(USAGE)
        val appName = newAppName.orNull ?: projectName.replace(Regex("(?<=[a-z0-9])(?=[A-Z])"), " ")

        if (!PACKAGE_REGEX.matches(pkg)) {
            throw GradleException("'$pkg' is not a valid package: use lowercase segments without underscores, e.g. com.mycompany.mynewapp")
        }
        if (!PROJECT_NAME_REGEX.matches(projectName)) {
            throw GradleException("'$projectName' is not a valid project name: use letters and digits only, e.g. MyNewApp")
        }
        if (pkg == OLD_PACKAGE || pkg.startsWith("$OLD_PACKAGE.")) {
            throw GradleException("The new package must differ from $OLD_PACKAGE and must not be nested inside it")
        }

        val sourceDirs = findPackageDirs(root)
        if (sourceDirs.isEmpty()) {
            throw GradleException("No source folder for $OLD_PACKAGE was found. Has the project already been renamed?")
        }

        val changedFiles = replaceInFiles(root, OLD_PACKAGE, pkg)
        logger.lifecycle("Replaced the package in $changedFiles files")

        sourceDirs.forEach { moveSourceDir(it, pkg) }
        logger.lifecycle("Moved ${sourceDirs.size} source folders to ${pkg.replace('.', '/')}")

        // Room names its exported schema folders after the database class, e.g.
        // data/schemas/<package>.data.local.database.AppDatabase.
        walk(root)
            .filter { it.isDirectory && it.name.startsWith("$OLD_PACKAGE.") }
            .toList()
            .forEach { dir ->
                val target = File(dir.parentFile, pkg + dir.name.removePrefix(OLD_PACKAGE))
                if (!dir.renameTo(target)) throw GradleException("Could not rename $dir")
                logger.lifecycle("Renamed ${dir.name} to ${target.name}")
            }

        replaceInFile(File(root, "settings.gradle.kts"), "rootProject.name = \"$OLD_PROJECT_NAME\"", "rootProject.name = \"$projectName\"")
        // Every string resource that shows the template's app name: the Android launcher label in
        // :androidApp and the Compose resources shared by all platforms.
        replaceInFiles(root, ">$OLD_APP_NAME</string>", ">${escapeAndroidString(appName)}</string>")
        // The iOS app name (Config.xcconfig) and the browser tab title (webApp index.html).
        replaceInFiles(root, "APP_NAME=$OLD_APP_NAME", "APP_NAME=$appName")
        replaceInFiles(root, "<title>$OLD_APP_NAME</title>", "<title>${escapeHtml(appName)}</title>")
        logger.lifecycle("Project name set to $projectName, app name set to $appName")

        // Incremental build outputs (KSP, Compose resources) still reference the old package and
        // break the next build, so the build folders of the modules are removed. build-logic keeps
        // its own because this task is running from it.
        walk(root)
            .filter { it.isFile && it.name == "build.gradle.kts" }
            .map { File(it.parentFile, "build") }
            .toList()
            .forEach { it.deleteRecursively() }
        File(root, ".kotlin").deleteRecursively()
        logger.lifecycle("Deleted the build folders of all modules")
        logger.lifecycle("Done. Sync Gradle, rebuild the project, and continue with docsagent/SETUP.md.")
    }

    /** Returns every `src/<sourceSet>/{kotlin,java}/<old package path>` folder outside the skipped folders. */
    private fun findPackageDirs(root: File): List<File> {
        val oldPath = OLD_PACKAGE.replace('.', File.separatorChar)
        return walk(root)
            .filter { it.isDirectory && it.path.endsWith(oldPath) }
            .filter { dir ->
                val sourceRoot = dir.sourceRoot()
                sourceRoot.name in setOf("kotlin", "java") && sourceRoot.parentFile?.parentFile?.name == "src"
            }
            .toList()
    }

    /** Moves the package folder [oldDir] to the path of [pkg] under the same source root and removes empty parents. */
    private fun moveSourceDir(oldDir: File, pkg: String) {
        val sourceRoot = oldDir.sourceRoot()
        val temp = File(sourceRoot, ".rename-tmp")
        if (!oldDir.renameTo(temp)) throw GradleException("Could not move $oldDir")
        deleteEmptyParents(oldDir.parentFile, sourceRoot)

        val target = File(sourceRoot, pkg.replace('.', File.separatorChar))
        if (target.exists()) {
            temp.copyRecursively(target, overwrite = false)
            temp.deleteRecursively()
        } else {
            target.parentFile.mkdirs()
            if (!temp.renameTo(target)) throw GradleException("Could not move $temp to $target")
        }
    }

    /** The source root (`.../src/main/kotlin`) that contains the template package folder [this]. */
    private fun File.sourceRoot(): File {
        var dir = this
        repeat(OLD_PACKAGE.count { it == '.' } + 1) { dir = dir.parentFile }
        return dir
    }

    /** Deletes [dir] and its parents while they are empty, stopping at [stopAt]. */
    private fun deleteEmptyParents(dir: File, stopAt: File) {
        var current: File? = dir
        while (current != null && current != stopAt && current.list()?.isEmpty() == true) {
            current.delete()
            current = current.parentFile
        }
    }

    /** Replaces [old] with [new] in every text file of the project and returns how many files changed. */
    private fun replaceInFiles(root: File, old: String, new: String): Int =
        walk(root)
            .filter { it.isFile && it.extension in TEXT_EXTENSIONS }
            .count { replaceInFile(it, old, new) }

    /** Replaces [old] with [new] in [file] and returns whether the file changed. */
    private fun replaceInFile(file: File, old: String, new: String): Boolean {
        if (!file.exists()) return false
        val text = file.readText()
        if (!text.contains(old)) return false
        file.writeText(text.replace(old, new))
        return true
    }

    /** Walks [root] while skipping build output, VCS and IDE folders, and `build-logic`. */
    private fun walk(root: File): Sequence<File> =
        root.walkTopDown().onEnter { dir ->
            dir == root || (dir.name !in SKIPPED_DIRS && dir != File(root, "build-logic"))
        }

    /** Escapes the characters that have a special meaning in Android string resources. */
    private fun escapeAndroidString(value: String): String =
        value.replace("&", "&amp;").replace("<", "&lt;").replace("'", "\\'").replace("\"", "\\\"")

    /** Escapes the characters that have a special meaning in HTML text. */
    private fun escapeHtml(value: String): String =
        value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private companion object {
        const val OLD_PACKAGE = "com.template.kmpstarter"
        const val OLD_PROJECT_NAME = "KmpStarter"
        const val OLD_APP_NAME = "KMP Starter"

        val PACKAGE_REGEX = Regex("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)+$")
        val PROJECT_NAME_REGEX = Regex("^[A-Za-z][A-Za-z0-9]*$")
        val TEXT_EXTENSIONS = setOf("kt", "kts", "xml", "toml", "pro", "properties", "swift", "xcconfig", "pbxproj", "plist", "html")
        val SKIPPED_DIRS = setOf("build", ".git", ".gradle", ".idea", ".kotlin", "node_modules", "kotlin-js-store", "DerivedData")

        const val USAGE = "Usage: ./gradlew renameProject -PnewPackage=com.mycompany.mynewapp -PnewProjectName=MyNewApp [-PnewAppName=\"My New App\"]"
    }
}
