
package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cappucare.model.Producto
import com.example.cappucare.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlertasUiState(
    val productos: List<Producto> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class AlertasViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(AlertasUiState())
    val estado: StateFlow<AlertasUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update {
                it.copy(cargando = true, error = null)
            }

            repository.obtenerStockBajo()
                .onSuccess { productos ->
                    _estado.update {
                        it.copy(
                            productos = productos,
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
        private val repository: ProductoRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertasViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}
