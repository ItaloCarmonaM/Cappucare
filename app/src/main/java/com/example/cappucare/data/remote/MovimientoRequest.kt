package com.example.cappucare.data.remote

import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.TipoMovimiento

data class MovimientoRequest(
    val productoId: Int,
    val usuarioId: Int,
    val usuarioNombre: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: MotivoSalida? = null
)