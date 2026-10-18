// Shared building blocks with no UI and no business logic.
//
//   config/       AppConfig, values the platform entry points pass in (debug flag, ...)
//   logging/      Kermit setup
//   result/       Resource, the outcome of a data operation
//   dispatcher/   DispatcherProvider, injectable coroutine dispatchers
//   util/         small Kotlin extensions and helpers
//   di/           commonModule (Koin)
plugins {
    alias(libs.plugins.starter.kmp.library)
}

kotlin {
    android {
        namespace = "com.template.kmpstarter.core.common"
    }
}
