package com.example.cappucare.repository

import androidx.room.withTransaction
import com.example.cappucare.data.AppDB
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.Movimiento
import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.model.ResultadoMovimiento
import com.example.cappucare.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

// Registro de movimientos y consulta del historial
class MovimientoRepository(private val db: AppDB) {

    private val productoDao = db.productoDao()
    private val movimientoDao = db.movimientoDao()

    fun obtenerHistorial(): Flow<List<MovimientoDetalle>> = movimientoDao.obtenerHistorial()

    // Registra un ingreso o salida y actualiza el stock
    suspend fun registrar(
        productoId: Int,
        usuarioId: Int,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: MotivoSalida?
    ): Result<ResultadoMovimiento> {
        return try {
            val resultado = db.withTransaction {
                val producto = productoDao.obtenerPorId(productoId)
                    ?: throw IllegalArgumentException("El producto no existe")

                require(cantidad > 0) { "La cantidad debe ser mayor a 0" }

                if (tipo == TipoMovimiento.SALIDA) {
                    // Toda salida lleva un motivo
                    requireNotNull(motivo) { "Debes indicar el motivo de la salida" }
                    // No puede superar el stock disponible
                    require(cantidad <= producto.cantidad) { "Stock insuficiente: disponible ${producto.cantidad}" }
                }

                val nuevaCantidad = if (tipo == TipoMovimiento.INGRESO) {
                    producto.cantidad + cantidad
                } else {
                    producto.cantidad - cantidad
                }

                // Actualiza el stock y deja el movimiento en el historial
                productoDao.actualizarCantidad(productoId, nuevaCantidad)
                movimientoDao.insertar(
                    Movimiento(
                        productoId = productoId,
                        usuarioId = usuarioId,
                        tipo = tipo,
                        cantidad = cantidad,
                        motivo = if (tipo == TipoMovimiento.SALIDA) motivo else null
                    )
                )

                val alcanzoMinimo = producto.cantidad > producto.nivelMinimo &&
                        nuevaCantidad <= producto.nivelMinimo

                ResultadoMovimiento(producto.copy(cantidad = nuevaCantidad), alcanzoMinimo)
            }
            Result.success(resultado)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }
}