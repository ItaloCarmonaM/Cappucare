package com.example.cappucare.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cappucare.model.Movimiento
import com.example.cappucare.model.MovimientoDetalle
import kotlinx.coroutines.flow.Flow

// Acceso al historial de movimientos
@Dao
interface MovimientoDao {

    @Insert
    suspend fun insertar(movimiento: Movimiento): Long

    // Historial completo, del más reciente al más antiguo
    @Query(
        "SELECT m.*, p.nombre AS productoNombre, u.nombre AS usuarioNombre " +
                "FROM movimientos m " +
                "INNER JOIN productos p ON m.productoId = p.id " +
                "INNER JOIN usuarios u ON m.usuarioId = u.id " +
                "ORDER BY m.fecha DESC"
    )
    fun obtenerHistorial(): Flow<List<MovimientoDetalle>>
}