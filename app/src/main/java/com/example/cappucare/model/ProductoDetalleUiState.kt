package com.example.cappucare.model

// Estado de la pantalla de detalle de un producto
data class ProductoDetalleUiState(
    val producto: Producto? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val eliminando: Boolean = false,       // Eliminación en curso
    val errorEliminar: String? = null      // El servidor rechazó la eliminación
)