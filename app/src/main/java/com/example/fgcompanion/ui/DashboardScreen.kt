package com.example.fgcompanion.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fgcompanion.viewmodel.FlightViewModel

@Composable
fun DashboardScreen(
    viewModel: FlightViewModel
) {
    val data by viewModel.flightData.collectAsStateWithLifecycle()
    val status by viewModel.connectionStatus.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startListening(5500)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        PFDScreen(
            data = data,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = status,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}