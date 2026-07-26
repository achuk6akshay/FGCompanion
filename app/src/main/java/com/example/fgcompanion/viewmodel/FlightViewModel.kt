package com.example.fgcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fgcompanion.repository.FlightRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FlightViewModel : ViewModel() {

    private val repository = FlightRepository()

    val flightData = repository.flightData
    val connectionStatus = repository.connectionStatus

    private var listenerJob: Job? = null

    fun startListening(port: Int = 5500) {

        if (listenerJob?.isActive == true) {
            return
        }

        listenerJob = viewModelScope.launch {
            repository.startListening(port)
        }
    }

    fun stopListening() {
        repository.stopListening()

        listenerJob?.cancel()
        listenerJob = null
    }

    override fun onCleared() {
        stopListening()
        super.onCleared()
    }
}