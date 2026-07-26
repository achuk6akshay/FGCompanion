package com.example.fgcompanion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fgcompanion.data.FlightData

@Composable
fun PFDScreen(
    data: FlightData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // Attitude indicator
        ArtificialHorizon(
            pitch = data.pitch,
            roll = data.roll,
            modifier = Modifier.fillMaxSize()
        )

        // IAS tape
        AirspeedTape(
            airspeed = data.airspeed,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(110.dp)
                .fillMaxSize()
        )

        // Altitude tape
        AltitudeTape(
            altitude = data.altitude,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(125.dp)
                .fillMaxSize()
        )
    }
}

