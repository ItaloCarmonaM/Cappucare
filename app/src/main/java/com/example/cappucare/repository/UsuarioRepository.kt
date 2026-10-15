package com.example.cappucare.repository

import com.example.cappucare.data.UsuarioDao
import com.example.cappucare.model.Usuario

// Acceso a los usuarios para el login simulado
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Devuelve el usuario si las credenciales son correctas, o null
    suspend fun autenticar(correo: String, clave: String): Usuario? {
        return usuarioDao.autenticar(correo.trim(), clave)
    }
}