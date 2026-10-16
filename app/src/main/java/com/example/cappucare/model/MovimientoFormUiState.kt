package com.example.cappucare.model

// Estado del formulario de movimiento
data class MovimientoFormUiState(
    val productos: List<Producto> = emptyList(), // Para elegir el producto
    val producto: Producto? = null,              // Producto seleccionado
    val tipo: TipoMovimiento = TipoMovimiento.INGRESO,
    val cantidad: String = "",
    val motivo: MotivoSalida? = null,
    val soloVenta: Boolean = false,
    val guardando: Boolean = false,
    val errorGeneral: String? = null,
    val errores: MovimientoFormErrores = MovimientoFormErrores()
)

data class MovimientoFormErrores(
    val producto: String? = null,
    val cantidad: String? = null,
    val motivo: String? = null
) {
    fun hayErrores(): Boolean = listOf(producto, cantidad, motivo).any { it != null }
}