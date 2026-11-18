package com.example.cappucare.data.remote

import com.example.cappucare.model.Perfil

data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil
)