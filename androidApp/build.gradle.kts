// Android entry point: App (starts Koin with the Android context) and MainActivity, which shows
// the shared App() from :app. Also holds the Android-only parts: manifest, launcher icon, and the
// release build setup. Keep it thin; everything else lives in the shared modules.
plugins {
    alias(libs.plugins.starter.android.application)
}

android {
    namespace = "com.template.kmpstarter"

    defaultConfig {
        applicationId = "com.template.kmpstarter"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.app)
    implementation(libs.compose.runtime)
}
