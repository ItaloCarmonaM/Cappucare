package com.example.cappucare.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Registro de un ingreso o salida de productos (historial)
@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.RESTRICT // El historial no queda sin su producto
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("productoId"), Index("usuarioId")]
)
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val usuarioId: Int,                 // Responsable del registro
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: MotivoSalida? = null,   // Solo aplica a las salidas
    val fecha: Long = System.currentTimeMillis()
)