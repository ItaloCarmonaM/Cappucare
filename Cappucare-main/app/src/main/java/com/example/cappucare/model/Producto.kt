package com.example.cappucare.model

// Producto o insumo médico (datos que entrega el microservicio de inventario)
data class Producto(
    val id: Int = 0,
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