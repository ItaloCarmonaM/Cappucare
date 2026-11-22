package com.example.cappucare.repository

import com.example.cappucare.data.remote.InventarioApiService
import com.example.cappucare.data.remote.MovimientoRequest
import com.example.cappucare.data.remote.llamarApi
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.model.ResultadoMovimiento
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.model.Usuario

// Registro de movimientos y consulta del historial
class MovimientoRepository(private val api: InventarioApiService) {

    suspend fun obtenerHistorial(): Result<List<MovimientoDetalle>> =
        llamarApi { api.obtenerMovimientos() }

    // El servidor valida RN3, RN4 y RN5 y actualiza el stock (RN6)
    suspend fun registrar(
        usuario: Usuario,
        productoId: Int,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: MotivoSalida?
    ): Result<ResultadoMovimiento> = llamarApi {
        api.registrarMovimiento(
            MovimientoRequest(
                productoId = productoId,
                usuarioId = usuario.id,
                usuarioNombre = usuario.nombre,
                tipo = tipo,
                cantidad = cantidad,
                motivo = if (tipo == TipoMovimiento.SALIDA) motivo else null
            )
        )
    }
}