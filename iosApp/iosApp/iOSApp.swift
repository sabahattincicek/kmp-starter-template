// iOS entry point. Starts the shared app (logging and Koin) once, then shows ContentView, which
// hosts the shared Compose UI. The Kotlin code comes from the ComposeApp framework built by :app.
import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        #if DEBUG
        IosEntryKt.startApp(isDebug: true)
        #else
        IosEntryKt.startApp(isDebug: false)
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
