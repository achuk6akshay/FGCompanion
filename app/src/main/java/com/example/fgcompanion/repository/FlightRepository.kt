package com.example.fgcompanion.repository

import com.example.fgcompanion.network.FGSocketClient

class FlightRepository {

    private val client = FGSocketClient()

    val flightData = client.flightData
    val connectionStatus = client.connectionStatus

    suspend fun startListening(port: Int = 5500) {
        client.startListening(port)
    }

    fun stopListening() {
        client.stopListening()
    }
}