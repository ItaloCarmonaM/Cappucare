package com.example.cappucare.model

// Resultado de registrar un movimiento de inventario
data class ResultadoMovimiento(
    val producto: Producto,
    val alcanzoNivelMinimo: Boolean
)