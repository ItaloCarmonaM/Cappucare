package com.example.cappucare.model

import androidx.room.Embedded

// Movimiento junto al nombre del producto y del responsable
data class MovimientoDetalle(
    @Embedded val movimiento: Movimiento,
    val productoNombre: String,
    val usuarioNombre: String
)