package com.example.cappucare.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Producto o insumo médico
@Entity(
    tableName = "productos",
    indices = [Index(value = ["codigo"], unique = true)] // RN1: código único
)
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val cantidad: Int,
    val unidadMedida: String,
    val nivelMinimo: Int,
    val ubicacion: String
) {
    // RN7: el estado se calcula a partir de las existencias
    val estado: EstadoProducto
        get() = when {
            cantidad <= 0 -> EstadoProducto.AGOTADO
            cantidad <= nivelMinimo -> EstadoProducto.STOCK_BAJO
            else -> EstadoProducto.DISPONIBLE
        }
}