package com.example.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.MovimientoFormErrores
import com.example.cappucare.model.MovimientoFormUiState
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Producto
import com.example.cappucare.model.ResultadoMovimiento
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.cappucare.viewmodel.validacion.Validadores

class MovimientoFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val productoRepository = getApplication<CappucareApp>().productoRepository
    private val movimientoRepository = getApplication<CappucareApp>().movimientoRepository

    // Producto preseleccionado por la ruta (0 significa ninguno)
    private val productoIdInicial: Int = savedStateHandle.get<Int>(Screen.ARG_PRODUCTO_ID) ?: 0

    // Estado interno mutable
    private val _estado = MutableStateFlow(MovimientoFormUiState())

    val estado: StateFlow<MovimientoFormUiState> = _estado

    init {
        cargarProductos()
    }

    // Carga el producto preseleccionado y la lista de productos para elegir
    private fun cargarProductos() {
        viewModelScope.launch {
            if (productoIdInicial != 0) {
                val inicial = productoRepository.obtenerPorId(productoIdInicial)
                _estado.update { it.copy(producto = inicial) }
            }
            productoRepository.obtenerTodos().collect { lista ->
                _estado.update { it.copy(productos = lista) }
            }
        }
    }

    fun configurarPerfil(perfil: Perfil) {
        if (perfil == Perfil.CONSULTOR) {
            _estado.update { it.copy(tipo = TipoMovimiento.SALIDA, motivo = MotivoSalida.VENTA, soloVenta = true) }
        }
    }

    fun onProductoChange(valor: Producto) {
        _estado.update { it.copy(producto = valor, errores = it.errores.copy(producto = null, cantidad = null)) }
    }

    fun onTipoChange(valor: TipoMovimiento) {
        if (_estado.value.soloVenta) return
        _estado.update {
            it.copy(
                tipo = valor,
                motivo = if (valor == TipoMovimiento.INGRESO) null else it.motivo,
                errores = it.errores.copy(cantidad = null, motivo = null)
            )
        }
    }

    fun onCantidadChange(valor: String) {
        _estado.update { it.copy(cantidad = valor, errores = it.errores.copy(cantidad = null)) }
    }

    fun onMotivoChange(valor: MotivoSalida) {
        if (_estado.value.soloVenta) return
        _estado.update { it.copy(motivo = valor, errores = it.errores.copy(motivo = null)) }
    }

    // Valida los campos y actualiza los errores
    // Valida los campos y actualiza los errores; retorna true si todo es correcto
    fun validarFormulario(): Boolean {
        val e = _estado.value
        val producto = e.producto
        val cantidad = e.cantidad.trim().toIntOrNull()
        val errorCantidad = Validadores.enteroPositivo(e.cantidad, "la cantidad") // RN4

        val errores = MovimientoFormErrores(
            producto = if (producto == null) "Selecciona un producto" else null,
            cantidad = when {
                errorCantidad != null -> errorCantidad
                e.tipo == TipoMovimiento.SALIDA && producto != null && cantidad != null &&
                        cantidad > producto.cantidad -> "Stock insuficiente: disponible ${producto.cantidad}" // RN3
                else -> null
            },
            // RN5: toda salida lleva un motivo
            motivo = if (e.tipo == TipoMovimiento.SALIDA && e.motivo == null) "Selecciona el motivo de la salida" else null
        )

        _estado.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    // Valida y registra el movimiento; entrega el resultado para notificar
    fun registrar(usuarioId: Int, onExito: (ResultadoMovimiento) -> Unit) {
        if (!validarFormulario()) return
        val e = _estado.value
        val producto = e.producto ?: return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorGeneral = null) }

            movimientoRepository.registrar(
                productoId = producto.id,
                usuarioId = usuarioId,
                tipo = e.tipo,
                cantidad = e.cantidad.trim().toInt(),
                motivo = e.motivo
            ).onSuccess { resultado ->
                _estado.update { it.copy(guardando = false) }
                onExito(resultado)
            }.onFailure { error ->
                _estado.update { it.copy(guardando = false, errorGeneral = error.message) }
            }
        }
    }
}