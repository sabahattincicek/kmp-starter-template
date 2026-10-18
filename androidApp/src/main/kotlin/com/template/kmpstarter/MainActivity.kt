package com.template.kmpstarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.touchlab.kermit.Logger
import com.template.kmpstarter.app.App as SharedApp

/**
 * The single activity of the app. Hosts the shared Compose UI from `:app`.
 */
class MainActivity : ComponentActivity() {

    private val log = Logger.withTag("MainActivity")

    /** Enables edge-to-edge drawing and shows the shared app. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.d { "onCreate: savedInstanceState=${if (savedInstanceState == null) "null" else "restored"}" }
        enableEdgeToEdge()
        setContent {
            SharedApp()
        }
    }
}
