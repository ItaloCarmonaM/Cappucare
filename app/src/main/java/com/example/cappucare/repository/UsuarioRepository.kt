package com.example.cappucare.repository

import com.example.cappucare.data.remote.LoginRequest
import com.example.cappucare.data.remote.UsuariosApiService
import com.example.cappucare.data.remote.llamarApi
import com.example.cappucare.model.Usuario

// Acceso al microservicio de usuarios
class UsuarioRepository(private val api: UsuariosApiService) {

    // Devuelve el usuario si las credenciales son correctas (error 401 si no)
    suspend fun autenticar(correo: String, clave: String): Result<Usuario> =
        llamarApi { api.login(LoginRequest(correo.trim(), clave)) }
}