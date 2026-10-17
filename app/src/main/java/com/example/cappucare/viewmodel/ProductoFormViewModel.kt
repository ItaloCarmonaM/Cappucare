package com.example.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.Producto
import com.example.cappucare.model.ProductoFormErrores
import com.example.cappucare.model.ProductoFormUiState
import com.example.cappucare.navigation.Screen
import com.example.cappucare.viewmodel.validacion.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = getApplication<CappucareApp>().productoRepository

    // Id recibido por la ruta (0 significa producto nuevo)
    private val productoId: Int = savedStateHandle.get<Int>(Screen.ARG_PRODUCTO_ID) ?: 0

    // Estado interno mutable
    private val _estado = MutableStateFlow(ProductoFormUiState(esEdicion = productoId != 0))
    // Estado expuesto para la UI
    val estado: StateFlow<ProductoFormUiState> = _estado

    // Producto original (solo en edición). El stock solo cambia mediante movimientos
    private var productoOriginal: Producto? = null

    init {
        if (productoId != 0) cargarProducto()
    }

    // Carga los datos del producto a editar en el formulario
    private fun cargarProducto() {
        viewModelScope.launch {
            repository.obtenerPorId(productoId)?.let { p ->
                productoOriginal = p
                _estado.update {
                    it.copy(
                        codigo = p.codigo,
                        nombre = p.nombre,
                        descripcion = p.descripcion,
                        categoria = p.categoria,
                        cantidad = p.cantidad.toString(),
                        unidadMedida = p.unidadMedida,
                        nivelMinimo = p.nivelMinimo.toString(),
                        ubicacion = p.ubicacion
                    )
                }
            }
        }
    }

    // Actualiza cada campo y limpia su error
    fun onCodigoChange(valor: String) {
        _estado.update { it.copy(codigo = valor, errores = it.errores.copy(codigo = null)) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun onCategoriaChange(valor: String) {
        _estado.update { it.copy(categoria = valor, errores = it.errores.copy(categoria = null)) }
    }

    fun onCantidadChange(valor: String) {
        _estado.update { it.copy(cantidad = valor, errores = it.errores.copy(cantidad = null)) }
    }

    fun onUnidadMedidaChange(valor: String) {
        _estado.update { it.copy(unidadMedida = valor, errores = it.errores.copy(unidadMedida = null)) }
    }

    fun onNivelMinimoChange(valor: String) {
        _estado.update { it.copy(nivelMinimo = valor, errores = it.errores.copy(nivelMinimo = null)) }
    }

    fun onUbicacionChange(valor: String) {
        _estado.update { it.copy(ubicacion = valor, errores = it.errores.copy(ubicacion = null)) }
    }

    // Valida los campos y actualiza los errores
    fun validarFormulario(): Boolean {
        val e = _estado.value

        val errores = ProductoFormErrores(
            codigo = Validadores.obligatorio(e.codigo, "el código"),
            nombre = Validadores.obligatorio(e.nombre, "el nombre"),
            categoria = Validadores.obligatorio(e.categoria, "la categoría"),
            cantidad = Validadores.enteroNoNegativo(e.cantidad, "la cantidad"), // RN2
            unidadMedida = Validadores.obligatorio(e.unidadMedida, "la unidad de medida"),
            nivelMinimo = Validadores.enteroNoNegativo(e.nivelMinimo, "el nivel mínimo"),
            ubicacion = Validadores.obligatorio(e.ubicacion, "la ubicación")
        )

        _estado.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    // Valida y guarda el producto; llama a onGuardado cuando termina bien
    fun guardar(onGuardado: () -> Unit) {
        // En edición, espera a que el producto termine de cargar
        if (_estado.value.esEdicion && productoOriginal == null) return
        if (!validarFormulario()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val e = _estado.value

            // RN1: el código no puede estar repetido
            if (repository.existeCodigo(e.codigo, productoId)) {
                _estado.update {
                    it.copy(guardando = false, errores = it.errores.copy(codigo = "Este código ya está en uso"))
                }
                return@launch
            }

            val original = productoOriginal
            if (original == null) {
                repository.crear(
                    Producto(
                        codigo = e.codigo.trim(),
                        nombre = e.nombre.trim(),
                        descripcion = e.descripcion.trim(),
                        categoria = e.categoria.trim(),
                        cantidad = e.cantidad.trim().toInt(),
                        unidadMedida = e.unidadMedida.trim(),
                        nivelMinimo = e.nivelMinimo.trim().toInt(),
                        ubicacion = e.ubicacion.trim()
                    )
                )
            } else {
                // Se conserva el stock original: solo cambia con movimientos (RN6)
                repository.actualizar(
                    original.copy(
                        codigo = e.codigo.trim(),
                        nombre = e.nombre.trim(),
                        descripcion = e.descripcion.trim(),
                        categoria = e.categoria.trim(),
                        unidadMedida = e.unidadMedida.trim(),
                        nivelMinimo = e.nivelMinimo.trim().toInt(),
                        ubicacion = e.ubicacion.trim()
                    )
                )
            }

            _estado.update { it.copy(guardando = false) }
            onGuardado()
        }
    }
}