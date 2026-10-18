// Test helpers shared by the unit tests of other modules. Added to a module with
// commonTest.dependencies { implementation(projects.core.testing) }; feature modules get it
// automatically.
plugins {
    alias(libs.plugins.starter.kmp.library)
}

kotlin {
    android {
        namespace = "com.template.kmpstarter.core.testing"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.kotlin.test)
            api(libs.kotlinx.coroutines.test)
            api(libs.turbine)
        }
        // On the JVM the kotlin.test annotations come from the JUnit binding; test source sets get
        // it automatically, this main source set needs it explicitly.
        androidMain.dependencies {
            api(libs.kotlin.test.junit)
        }
    }
}
