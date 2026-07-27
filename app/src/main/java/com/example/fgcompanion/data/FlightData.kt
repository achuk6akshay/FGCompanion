package com.example.fgcompanion.data

data class FlightData(

    // Position
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,

    // Speed
    val airspeed: Double = 0.0,       // IAS
    val trueAirspeed: Double = 0.0,   // TAS
    val groundSpeed: Double = 0.0,    // GS

    // Attitude / direction
    val heading: Double = 0.0,
    val pitch: Double = 0.0,
    val roll: Double = 0.0,
    val magneticTrack: Double = 0.0,

    // Vertical
    val verticalSpeed: Double = 0.0,

    val autopilotOn: Boolean = false,
    val gearDown: Boolean = false,

    val flapPosition: Double = 0.0,
    val throttle: Double = 0.0,
    val baroInHg: Double = 29.92,
    val altitudeAgl: Double = 0.0

)