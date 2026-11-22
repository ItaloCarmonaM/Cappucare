package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.MovimientoFormErrores
import com.example.cappucare.model.MovimientoFormUiState
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Producto
import com.example.cappucare.model.ResultadoMovimiento
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.model.Usuario
import com.example.cappucare.navigation.Screen
import com.example.cappucare.repository.MovimientoRepository
import com.example.cappucare.repository.ProductoRepository
import com.example.cappucare.viewmodel.validacion.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Formulario para registrar un ingreso o una salida.
// productoIdInicial es el producto preseleccionado por la ruta (0 significa ninguno)
class MovimientoFormViewModel(
    private val productoRepository: ProductoRepository,
    private val movimientoRepository: MovimientoRepository,
    private val productoIdInicial: Int
) : ViewModel() {

    // Parte "cargando" hasta traer la lista de productos del servidor
    private val _estado = MutableStateFlow(MovimientoFormUiState(cargando = true))
    val estado: StateFlow<MovimientoFormUiState> = _estado.asStateFlow()

    init {
        cargarProductos()
    }

    // Carga la lista de productos para elegir y deja seleccionado el de la ruta (también sirve para reintentar)
    fun cargarProductos() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errorCarga = null) }

            productoRepository.obtenerTodos()
                .onSuccess { lista ->
                    _estado.update { actual ->
                        // Si ya había uno elegido se mantiene, con su stock actualizado
                        val idSeleccionado = actual.producto?.id ?: productoIdInicial
                        actual.copy(
                            productos = lista,
                            producto = lista.firstOrNull { p -> p.id == idSeleccionado } ?: actual.producto,
                            cargando = false
                        )
                    }
                }
                .onFailure { error ->
                    _estado.update { it.copy(cargando = false, errorCarga = MensajesApi.de(error)) }
                }
        }
    }

    // El Consultor solo puede registrar ventas (RN9)
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
    fun registrar(usuario: Usuario, onExito: (ResultadoMovimiento) -> Unit) {
        if (_estado.value.guardando) return
        if (!validarFormulario()) return
        val e = _estado.value
        val producto = e.producto ?: return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorGeneral = null) }

            movimientoRepository.registrar(
                usuario = usuario,
                productoId = producto.id,
                tipo = e.tipo,
                cantidad = e.cantidad.trim().toInt(),
                motivo = e.motivo
            )
                .onSuccess { resultado ->
                    _estado.update { it.copy(guardando = false) }
                    onExito(resultado)
                }
                .onFailure { error ->
                    // El servidor también valida RN3, RN4 y RN5: se muestra su mensaje
                    _estado.update { it.copy(guardando = false, errorGeneral = MensajesApi.de(error)) }
                }
        }
    }

    companion object {
        // El producto preseleccionado llega por la ruta de navegación
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CappucareApp
                val productoId = createSavedStateHandle().get<Int>(Screen.ARG_PRODUCTO_ID) ?: 0
                MovimientoFormViewModel(app.productoRepository, app.movimientoRepository, productoId)
            }
        }
    }
}
