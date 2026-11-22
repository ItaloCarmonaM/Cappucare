
package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.repository.MovimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val movimientos: List<MovimientoDetalle> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class HistorialViewModel(
    private val repository: MovimientoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(HistorialUiState())
    val estado: StateFlow<HistorialUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update {
                it.copy(cargando = true, error = null)
            }

            repository.obtenerHistorial()
                .onSuccess { movimientos ->
                    _estado.update {
                        it.copy(
                            movimientos = movimientos,
                            cargando = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = MensajesApi.de(error)
                        )
                    }
                }
        }
    }

    class Factory(
        private val repository: MovimientoRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistorialViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HistorialViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}
