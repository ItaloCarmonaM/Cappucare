package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.HistorialUiState
import com.example.cappucare.repository.MovimientoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Historial de movimientos, del más reciente al más antiguo (el servidor ya los entrega ordenados).
// No consulta al crearse: la pantalla llama a cargar() cada vez que aparece
class HistorialViewModel(
    private val repository: MovimientoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(HistorialUiState())
    val estado: StateFlow<HistorialUiState> = _estado.asStateFlow()

    private var cargaJob: Job? = null

    // Consulta el historial (también sirve para reintentar tras un error)
    fun cargar() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            repository.obtenerHistorial()
                .onSuccess { movimientos ->
                    _estado.update { it.copy(movimientos = movimientos, cargando = false) }
                }
                .onFailure { error ->
                    // Se conservan los movimientos que ya se estaban mostrando
                    _estado.update { it.copy(cargando = false, error = MensajesApi.de(error)) }
                }
        }
    }

    companion object {
        // Crea el ViewModel con el repositorio que guarda CappucareApp
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CappucareApp
                HistorialViewModel(app.movimientoRepository)
            }
        }
    }
}
