// Shared UI used by every feature module, on every platform.
//
//   theme/        colors, typography, AppTheme
//   components/   composables used by several screens (ErrorView, LoadingView, ...)
//   text/         UiText, user-facing text passed from ViewModels
//   base/         base classes for ViewModels or screens, if the project wants them
//
// Strings live in src/commonMain/composeResources/values/strings.xml.
plugins {
    alias(libs.plugins.starter.kmp.library)
    alias(libs.plugins.starter.kmp.compose)
}

kotlin {
    android {
        namespace = "com.template.kmpstarter.presentation.ui"
    }
}

compose.resources {
    packageOfResClass = "com.template.kmpstarter.presentation.ui.resources"
}
