package com.pingtest.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import com.pingtest.app.ui.AppState
import com.pingtest.app.ui.HomeScreen
import com.pingtest.app.ui.LocalStrings
import com.pingtest.app.ui.PingTestTheme

class MainActivity : ComponentActivity() {

    private val notifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* sonucu yok say */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Arka plan servisi bildirimi için bildirim izni (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val appState = AppState.get(applicationContext)
            CompositionLocalProvider(LocalStrings provides appState.strings) {
                PingTestTheme {
                    HomeScreen(appState)
                }
            }
        }
    }
}
