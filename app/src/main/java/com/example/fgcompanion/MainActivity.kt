package com.example.fgcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.fgcompanion.ui.DashboardScreen
import com.example.fgcompanion.ui.theme.FGCompanionTheme
import com.example.fgcompanion.viewmodel.FlightViewModel

class MainActivity : ComponentActivity() {

    private val flightViewModel: FlightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FGCompanionTheme {
                DashboardScreen(
                    viewModel = flightViewModel
                )
            }
        }
    }
}