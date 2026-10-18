// Root build file. Plugins are declared here once with `apply false` so every module resolves them
// from the same classloader; the modules apply them through the convention plugins in build-logic.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false

    // Adds the `renameProject` task used once after creating a project from this template.
    alias(libs.plugins.starter.rename)
}
