package com.example.fgcompanion.network

import com.example.fgcompanion.data.FlightData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

class FGSocketClient {

    private val _flightData = MutableStateFlow(FlightData())
    val flightData: StateFlow<FlightData> = _flightData.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Not listening")
    val connectionStatus: StateFlow<String> =
        _connectionStatus.asStateFlow()

    private var socket: DatagramSocket? = null

    suspend fun startListening(port: Int = 5500) {
        withContext(Dispatchers.IO) {

            try {
                socket = DatagramSocket(port)

                _connectionStatus.value =
                    "Listening on UDP port $port"

                val buffer = ByteArray(2048)

                while (currentCoroutineContext().isActive) {

                    val packet = DatagramPacket(
                        buffer,
                        buffer.size
                    )

                    socket?.receive(packet)

                    val message = String(
                        packet.data,
                        packet.offset,
                        packet.length,
                        Charsets.UTF_8
                    ).trim()

                    parseFlightData(message)?.let { data ->
                        _flightData.value = data

                        _connectionStatus.value =
                            "Receiving FlightGear data"
                    }
                }

            } catch (e: SocketException) {

                if (currentCoroutineContext().isActive) {
                    _connectionStatus.value =
                        "UDP error: ${e.message}"
                }

            } catch (e: Exception) {

                _connectionStatus.value =
                    "Error: ${e.message}"

            } finally {

                socket?.close()
                socket = null
            }
        }
    }

    private fun parseFlightData(line: String): FlightData? {

        return try {

            val values = line
                .trim()
                .split(",")

            val size = 14

            if (values.size < size) return null

            FlightData(
                latitude = values[0].trim().toDouble(),
                longitude = values[1].trim().toDouble(),
                altitude = values[2].trim().toDouble(),

                airspeed = values[3].trim().toDouble(),

                heading = values[4].trim().toDouble(),
                pitch = values[5].trim().toDouble(),
                roll = values[6].trim().toDouble(),

                verticalSpeed = values[7].trim().toDouble(),

                trueAirspeed = values[8].trim().toDouble(),
                groundSpeed = values[9].trim().toDouble(),
                magneticTrack = values[10].trim().toDouble(),
                autopilotOn =
                        values[11].trim() == "1" ||
                        values[11].trim().equals("true", ignoreCase = true),
                gearDown =
                        values[12].trim() == "1" ||
                        values[12].trim().equals("true", ignoreCase = true),

                flapPosition = values[13].trim().toDouble()
            )

        } catch (e: Exception) {
            null
        }
    }

    fun stopListening() {

        socket?.close()
        socket = null

        _connectionStatus.value = "Not listening"
    }
}