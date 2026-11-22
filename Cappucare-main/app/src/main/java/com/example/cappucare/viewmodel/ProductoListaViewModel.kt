
package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.ProductoListaUiState
import com.example.cappucare.repository.ProductoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoListaViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(ProductoListaUiState())
    val estado: StateFlow<ProductoListaUiState> = _estado.asStateFlow()

    private var busquedaJob: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            repository.obtenerTodos()
                .onSuccess { productos ->
                    _estado.update {
                        it.copy(productos = productos, cargando = false, error = null)
                    }
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(cargando = false, error = MensajesApi.de(error))
                    }
                }
        }
    }

    fun onBusquedaChange(valor: String) {
        _estado.update { it.copy(busqueda = valor) }

        busquedaJob?.cancel()
        busquedaJob = viewModelScope.launch {
            delay(350)

            _estado.update { it.copy(cargando = true, error = null) }

            val resultado = if (valor.isBlank()) {
                repository.obtenerTodos()
            } else {
                repository.buscar(valor)
            }

            resultado
                .onSuccess { productos ->
                    _estado.update {
                        it.copy(productos = productos, cargando = false, error = null)
                    }
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(cargando = false, error = MensajesApi.de(error))
                    }
                }
        }
    }

    companion object {
        val Factory = androidx.lifecycle.viewmodel.viewModelFactory {
            androidx.lifecycle.viewmodel.initializer {
                val app = this[
                    androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as CappucareApp

                ProductoListaViewModel(app.productoRepository)
            }
        }
    }
}
