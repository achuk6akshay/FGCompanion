package com.example.fgcompanion

import android.view.WindowManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.fgcompanion.ui.DashboardScreen
import com.example.fgcompanion.ui.theme.FGCompanionTheme
import com.example.fgcompanion.viewmodel.FlightViewModel
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class MainActivity : ComponentActivity() {

    private val flightViewModel: FlightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Hide the status and navigation bars
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // Optional: make it behave like "immersive mode" (swiping from edge shows bars temporarily)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            FGCompanionTheme {
                DashboardScreen(
                    viewModel = flightViewModel
                )
            }
        }
    }
}