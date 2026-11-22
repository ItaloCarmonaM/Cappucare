package com.example.cappucare.data.remote

import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.model.Producto
import com.example.cappucare.model.ResultadoMovimiento
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints del microservicio de inventario (productos y movimientos)
interface InventarioApiService {

    // Si buscar es null, devuelve todos los productos
    @GET("api/productos")
    suspend fun obtenerProductos(@Query("buscar") buscar: String? = null): List<Producto>

    @GET("api/productos/stock-bajo")
    suspend fun obtenerStockBajo(): List<Producto>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Int): Producto

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: Producto): Producto

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Int, @Body producto: Producto): Producto

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int)

    @GET("api/movimientos")
    suspend fun obtenerMovimientos(): List<MovimientoDetalle>

    @POST("api/movimientos")
    suspend fun registrarMovimiento(@Body request: MovimientoRequest): ResultadoMovimiento
}