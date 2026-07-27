package com.example.fgcompanion.ui


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fgcompanion.viewmodel.FlightViewModel

@Composable
fun DashboardScreen(
    viewModel: FlightViewModel
) {
    val data by viewModel.flightData.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val dataIncoming by viewModel.dataIncoming.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // App is foregrounded, screen is active
                    viewModel.startListening(5500)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // App goes background (Home, switch apps)
                    viewModel.stopListening()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopListening()
        }
    }

    FlightDisplay(
        data = data,
        dataIncoming = dataIncoming,
        modifier = Modifier.fillMaxSize()
    )
}


