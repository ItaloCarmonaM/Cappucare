
package com.example.cappucare.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.MovimientoFormErrores
import com.example.cappucare.model.MovimientoFormUiState
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Producto
import com.example.cappucare.model.ResultadoMovimiento
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.navigation.Screen
import com.example.cappucare.repository.MovimientoRepository
import com.example.cappucare.repository.ProductoRepository
import com.example.cappucare.viewmodel.validacion.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovimientoFormViewModel(
    private val productoRepository: ProductoRepository,
    private val movimientoRepository: MovimientoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productoIdInicial: Int =
        savedStateHandle.get<Int>(Screen.ARG_PRODUCTO_ID) ?: 0

    private val _estado = MutableStateFlow(MovimientoFormUiState())
    val estado: StateFlow<MovimientoFormUiState> = _estado.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _estado.update {
                it.copy(cargando = true, errorGeneral = null)
            }

            if (productoIdInicial != 0) {
                productoRepository.obtenerPorId(productoIdInicial)
                    .onSuccess { producto ->
                        _estado.update {
                            it.copy(producto = producto)
                        }
                    }
                    .onFailure { error ->
                        _estado.update {
                            it.copy(errorGeneral = MensajesApi.de(error))
                        }
                    }
            }

            productoRepository.obtenerTodos()
                .onSuccess { productos ->
                    _estado.update {
                        it.copy(
                            productos = productos,
                            cargando = false,
                            errorGeneral = null
                        )
                    }
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            cargando = false,
                            errorGeneral = MensajesApi.de(error)
                        )
                    }
                }
        }
    }

    fun configurarPerfil(perfil: Perfil) {
        if (perfil == Perfil.CONSULTOR) {
            _estado.update {
                it.copy(
                    tipo = TipoMovimiento.SALIDA,
                    motivo = MotivoSalida.VENTA,
                    soloVenta = true
                )
            }
        }
    }

    fun onProductoChange(valor: Producto) {
        _estado.update {
            it.copy(
                producto = valor,
                errores = it.errores.copy(
                    producto = null,
                    cantidad = null
                )
            )
        }
    }

    fun onTipoChange(valor: TipoMovimiento) {
        if (_estado.value.soloVenta) return

        _estado.update {
            it.copy(
                tipo = valor,
                motivo = if (valor == TipoMovimiento.INGRESO) null else it.motivo,
                errores = it.errores.copy(
                    cantidad = null,
                    motivo = null
                )
            )
        }
    }

    fun onCantidadChange(valor: String) {
        _estado.update {
            it.copy(
                cantidad = valor,
                errores = it.errores.copy(cantidad = null)
            )
        }
    }

    fun onMotivoChange(valor: MotivoSalida) {
        if (_estado.value.soloVenta) return

        _estado.update {
            it.copy(
                motivo = valor,
                errores = it.errores.copy(motivo = null)
            )
        }
    }

    fun validarFormulario(): Boolean {
        val e = _estado.value
        val producto = e.producto
        val cantidad = e.cantidad.trim().toIntOrNull()
        val errorCantidad = Validadores.enteroPositivo(
            e.cantidad,
            "la cantidad"
        )

        val errores = MovimientoFormErrores(
            producto = if (producto == null) {
                "Selecciona un producto"
            } else {
                null
            },
            cantidad = when {
                errorCantidad != null -> errorCantidad
                e.tipo == TipoMovimiento.SALIDA &&
                        producto != null &&
                        cantidad != null &&
                        cantidad > producto.cantidad ->
                    "Stock insuficiente: disponible ${producto.cantidad}"
                else -> null
            },
            motivo = if (
                e.tipo == TipoMovimiento.SALIDA && e.motivo == null
            ) {
                "Selecciona el motivo de la salida"
            } else {
                null
            }
        )

        _estado.update {
            it.copy(errores = errores)
        }

        return !errores.hayErrores()
    }

    fun registrar(
        usuarioId: Int,
        onExito: (ResultadoMovimiento) -> Unit
    ) {
        if (!validarFormulario()) return

        val e = _estado.value
        val producto = e.producto ?: return
        val usuario = Sesion.usuario

        if (usuario == null || usuario.id != usuarioId) {
            _estado.update {
                it.copy(errorGeneral = "No hay un usuario válido en la sesión")
            }
            return
        }

        viewModelScope.launch {
            _estado.update {
                it.copy(guardando = true, errorGeneral = null)
            }

            movimientoRepository.registrar(
                usuario = usuario,
                productoId = producto.id,
                tipo = e.tipo,
                cantidad = e.cantidad.trim().toInt(),
                motivo = e.motivo
            )
                .onSuccess { resultado ->
                    _estado.update {
                        it.copy(guardando = false)
                    }
                    onExito(resultado)
                }
                .onFailure { error ->
                    _estado.update {
                        it.copy(
                            guardando = false,
                            errorGeneral = MensajesApi.de(error)
                        )
                    }
                }
        }
    }

    companion object {
        fun Factory(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle?,
            productoRepository: ProductoRepository,
            movimientoRepository: MovimientoRepository
        ): ViewModelProvider.Factory {

            return object : AbstractSavedStateViewModelFactory(
                owner,
                defaultArgs
            ) {
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    if (modelClass.isAssignableFrom(
                            MovimientoFormViewModel::class.java
                        )
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        return MovimientoFormViewModel(
                            productoRepository,
                            movimientoRepository,
                            handle
                        ) as T
                    }

                    throw IllegalArgumentException("ViewModel desconocido")
                }
            }
        }
    }
}
