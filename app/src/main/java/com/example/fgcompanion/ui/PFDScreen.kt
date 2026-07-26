package com.example.fgcompanion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fgcompanion.data.FlightData

@Composable
fun PFDScreen(
    data: FlightData,
    modifier: Modifier = Modifier
) {
    FlightDisplay(
        data = data,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}