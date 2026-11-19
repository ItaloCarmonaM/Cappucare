package com.example.cappucare.data.remote

import com.example.cappucare.model.Perfil

// Cuerpo para crear o editar un usuario
data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil
)