package com.example.cappucare.repository

import com.example.cappucare.data.ProductoDao
import com.example.cappucare.model.Producto
import kotlinx.coroutines.flow.Flow

// Acceso a los datos de los productos
class ProductoRepository(private val productoDao: ProductoDao) {

    fun obtenerTodos(): Flow<List<Producto>> = productoDao.obtenerTodos()

    fun buscar(texto: String): Flow<List<Producto>> = productoDao.buscar(texto.trim())

    fun obtenerStockBajo(): Flow<List<Producto>> = productoDao.obtenerStockBajo()

    suspend fun obtenerPorId(id: Int): Producto? = productoDao.obtenerPorId(id)

    // Indica si el código ya está en uso por otro producto
    // Al editar, se pasa el id actual para que no se compare consigo mismo
    suspend fun existeCodigo(codigo: String, idActual: Int = 0): Boolean {
        val encontrado = productoDao.obtenerPorCodigo(codigo.trim())
        return encontrado != null && encontrado.id != idActual
    }

    suspend fun crear(producto: Producto): Long = productoDao.insertar(producto)

    suspend fun actualizar(producto: Producto) = productoDao.actualizar(producto)
}