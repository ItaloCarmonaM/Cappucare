package com.example.cappucare.model

data class ProductoDetalleUiState(
    val producto: Producto? = null,
    val cargando: Boolean = false,
    val error: String? = null
)