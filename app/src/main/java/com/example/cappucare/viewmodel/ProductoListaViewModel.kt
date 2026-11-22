package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

// Lista de productos con búsqueda por nombre o código.
// No consulta al crearse: la pantalla llama a cargar() cada vez que aparece
class ProductoListaViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(ProductoListaUiState())
    val estado: StateFlow<ProductoListaUiState> = _estado.asStateFlow()

    // Consulta en curso: al lanzar otra se cancela la anterior
    private var consultaJob: Job? = null

    // Consulta con el texto de búsqueda actual (también sirve para reintentar tras un error)
    fun cargar() {
        consultar(pausaMs = 0)
    }

    // Actualiza el texto y espera una pausa antes de consultar, para no llamar al servidor en cada letra
    fun onBusquedaChange(valor: String) {
        _estado.update { it.copy(busqueda = valor) }
        consultar(pausaMs = PAUSA_BUSQUEDA_MS)
    }

    private fun consultar(pausaMs: Long) {
        consultaJob?.cancel()
        consultaJob = viewModelScope.launch {
            if (pausaMs > 0) delay(pausaMs)

            _estado.update { it.copy(cargando = true, error = null) }

            val texto = _estado.value.busqueda
            val resultado = if (texto.isBlank()) {
                repository.obtenerTodos()
            } else {
                repository.buscar(texto)
            }

            resultado
                .onSuccess { productos ->
                    _estado.update { it.copy(productos = productos, cargando = false) }
                }
                .onFailure { error ->
                    // Se conservan los productos que ya se estaban mostrando
                    _estado.update { it.copy(cargando = false, error = MensajesApi.de(error)) }
                }
        }
    }

    companion object {
        private const val PAUSA_BUSQUEDA_MS = 300L

        // Crea el ViewModel con el repositorio que guarda CappucareApp
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CappucareApp
                ProductoListaViewModel(app.productoRepository)
            }
        }
    }
}
