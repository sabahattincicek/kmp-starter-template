plugins {
    `kotlin-dsl`
}

group = "starter.buildlogic"

dependencies {
    // Only compiled against; the root build puts the real plugins on the classpath.
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.starter.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("kmpLibrary") {
            id = libs.plugins.starter.kmp.library.get().pluginId
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = libs.plugins.starter.kmp.compose.get().pluginId
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = libs.plugins.starter.kmp.feature.get().pluginId
            implementationClass = "KmpFeatureConventionPlugin"
        }
        register("rename") {
            id = libs.plugins.starter.rename.get().pluginId
            implementationClass = "RenameProjectConventionPlugin"
        }
    }
}
