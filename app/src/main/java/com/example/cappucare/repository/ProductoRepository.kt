package com.example.cappucare.repository

import com.example.cappucare.data.remote.InventarioApiService
import com.example.cappucare.data.remote.llamarApi
import com.example.cappucare.model.Producto

// Acceso a los productos del microservicio de inventario
class ProductoRepository(private val api: InventarioApiService) {

    suspend fun obtenerTodos(): Result<List<Producto>> =
        llamarApi { api.obtenerProductos() }

    suspend fun buscar(texto: String): Result<List<Producto>> =
        llamarApi { api.obtenerProductos(texto.trim()) }

    suspend fun obtenerStockBajo(): Result<List<Producto>> =
        llamarApi { api.obtenerStockBajo() }

    suspend fun obtenerPorId(id: Int): Result<Producto> =
        llamarApi { api.obtenerProducto(id) }

    // Error 409 si el código ya está en uso (RN1)
    suspend fun crear(producto: Producto): Result<Producto> =
        llamarApi { api.crearProducto(producto) }

    suspend fun actualizar(producto: Producto): Result<Producto> =
        llamarApi { api.actualizarProducto(producto.id, producto) }

    // Error 409 si el producto tiene movimientos registrados
    suspend fun eliminar(id: Int): Result<Unit> =
        llamarApi { api.eliminarProducto(id) }
}