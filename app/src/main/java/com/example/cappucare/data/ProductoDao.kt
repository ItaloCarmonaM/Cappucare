package com.example.cappucare.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cappucare.model.Producto
import kotlinx.coroutines.flow.Flow

// Acceso a los datos de los productos
@Dao
interface ProductoDao {

    // Error si código ya existe
    @Insert
    suspend fun insertar(producto: Producto): Long

    @Update
    suspend fun actualizar(producto: Producto)

    // Flow: la lista se actualiza cuando cambia la base de datos
    @Query("SELECT * FROM productos ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query(
        "SELECT * FROM productos " +
                "WHERE nombre LIKE '%' || :texto || '%' OR codigo LIKE '%' || :texto || '%' " +
                "ORDER BY nombre"
    )
    fun buscar(texto: String): Flow<List<Producto>>

    // Observa un producto: se actualiza solo cuando cambia en la base de datos
    @Query("SELECT * FROM productos WHERE id = :id")
    fun observarPorId(id: Int): Flow<Producto?>

    // Productos en stock bajo o agotados
    @Query("SELECT * FROM productos WHERE cantidad <= nivelMinimo ORDER BY cantidad")
    fun obtenerStockBajo(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): Producto?

    // Se usa al registrar un movimiento para actualizar el stock
    @Query("UPDATE productos SET cantidad = :cantidad WHERE id = :id")
    suspend fun actualizarCantidad(id: Int, cantidad: Int)
}