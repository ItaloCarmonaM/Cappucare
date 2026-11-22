package com.example.cappucare.model

// Movimiento junto al nombre del producto y del responsable
data class MovimientoDetalle(
    val movimiento: Movimiento,
    val productoNombre: String,
    val usuarioNombre: String
)