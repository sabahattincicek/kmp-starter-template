// Domain layer: the business rules of the app, in plain Kotlin shared by every platform.
//
//   model/        business models
//   repository/   repository interfaces (implemented in :data)
//   usecase/      use cases, one action each, called by the ViewModels
//   di/           domainModule (Koin), registers the use cases
//
// Depends only on :core:common. :data implements it, the presentation modules call it.
plugins {
    alias(libs.plugins.starter.kmp.library)
}

kotlin {
    android {
        namespace = "com.template.kmpstarter.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
        }
        commonTest.dependencies {
            implementation(projects.core.testing)
        }
    }
}
