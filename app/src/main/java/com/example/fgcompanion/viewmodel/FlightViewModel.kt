package com.example.fgcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fgcompanion.repository.FlightRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class FlightViewModel : ViewModel() {

    private val repository = FlightRepository()

    val flightData = repository.flightData
    val connectionStatus = repository.connectionStatus

    private val _dataIncoming = MutableStateFlow(false)
    val dataIncoming: StateFlow<Boolean> = _dataIncoming.asStateFlow()

    private var lastPacketTime = 0L

    private var listenerJob: Job? = null
    private var timeoutJob: Job? = null
    private var dataMonitorJob: Job? = null

    private fun startDataMonitor() {
        dataMonitorJob?.cancel()

        dataMonitorJob = viewModelScope.launch {
            repository.flightData.collect {
                lastPacketTime = System.currentTimeMillis()
                _dataIncoming.value = true
            }
        }
    }

    private fun startDataTimeoutMonitor() {
        timeoutJob?.cancel()

        timeoutJob = viewModelScope.launch {
            while (true) {

                delay(500.milliseconds)

                if (lastPacketTime > 0L) {

                    val elapsed =
                        System.currentTimeMillis() - lastPacketTime

                    if (elapsed > 2000) {
                        _dataIncoming.value = false
                    }
                }
            }
        }
    }

    fun startListening(port: Int = 5500) {

        if (listenerJob?.isActive == true) {
            return
        }

        lastPacketTime = 0L
        _dataIncoming.value = false

        listenerJob = viewModelScope.launch {
            repository.startListening(port)
        }

        startDataMonitor()
        startDataTimeoutMonitor()
    }

    fun stopListening() {

        repository.stopListening()

        listenerJob?.cancel()
        listenerJob = null

        dataMonitorJob?.cancel()
        dataMonitorJob = null

        timeoutJob?.cancel()
        timeoutJob = null

        lastPacketTime = 0L
        _dataIncoming.value = false
    }

    override fun onCleared() {
        stopListening()
        super.onCleared()
    }
}