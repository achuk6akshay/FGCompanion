package com.example.fgcompanion.data

data class FlightData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val airspeed: Double = 0.0,
    val heading: Double = 0.0,
    val pitch: Double = 0.0,
    val roll: Double = 0.0,
    val verticalSpeed: Double = 0.0
)