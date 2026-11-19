package com.example.cappucare.model

// Registro de un ingreso o salida de productos
data class Movimiento(
    val id: Int = 0,
    val productoId: Int,
    val usuarioId: Int,                 // Responsable del registro
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: MotivoSalida? = null,   // Solo aplica a las salidas
    val fecha: Long = System.currentTimeMillis()
)