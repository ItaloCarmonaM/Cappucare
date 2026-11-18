package com.example.cappucare.data.remote

import com.example.cappucare.model.Usuario
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del microservicio de usuarios
interface UsuariosApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Usuario

    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): Usuario

    @POST("api/usuarios")
    suspend fun crearUsuario(@Body request: UsuarioRequest): Usuario

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Int, @Body request: UsuarioRequest): Usuario

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int)
}