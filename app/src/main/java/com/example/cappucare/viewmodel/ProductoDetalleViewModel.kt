package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.ProductoDetalleUiState
import com.example.cappucare.navigation.Screen
import com.example.cappucare.repository.ProductoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Detalle de un producto. La pantalla llama a cargar() cada vez que aparece,
// así el stock se ve actualizado después de editar o registrar un movimiento
class ProductoDetalleViewModel(
    private val repository: ProductoRepository,
    private val productoId: Int
) : ViewModel() {

    private val _estado = MutableStateFlow(ProductoDetalleUiState())
    val estado: StateFlow<ProductoDetalleUiState> = _estado.asStateFlow()

    private var cargaJob: Job? = null

    fun cargar() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            repository.obtenerPorId(productoId)
                .onSuccess { producto ->
                    _estado.update { it.copy(producto = producto, cargando = false) }
                }
                .onFailure { error ->
                    _estado.update { it.copy(cargando = false, error = MensajesApi.de(error)) }
                }
        }
    }

    // Elimina el producto; llama a onEliminado cuando termina bien
    fun eliminar(onEliminado: () -> Unit) {
        if (_estado.value.eliminando) return

        viewModelScope.launch {
            _estado.update { it.copy(eliminando = true, errorEliminar = null) }

            repository.eliminar(productoId)
                .onSuccess {
                    _estado.update { it.copy(eliminando = false) }
                    onEliminado()
                }
                .onFailure { error ->
                    // Por ejemplo 409: el producto tiene movimientos y no se puede eliminar
                    _estado.update { it.copy(eliminando = false, errorEliminar = MensajesApi.de(error)) }
                }
        }
    }

    // Cierra el aviso de error de la eliminación
    fun descartarErrorEliminar() {
        _estado.update { it.copy(errorEliminar = null) }
    }

    companion object {
        // El id del producto llega por la ruta de navegación
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CappucareApp
                val productoId = createSavedStateHandle().get<Int>(Screen.ARG_PRODUCTO_ID) ?: 0
                ProductoDetalleViewModel(app.productoRepository, productoId)
            }
        }
    }
}