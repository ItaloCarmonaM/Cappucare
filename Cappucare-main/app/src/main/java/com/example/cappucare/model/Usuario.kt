package com.example.cappucare.model

// Usuario que devuelve el microservicio de usuarios
data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val perfil: Perfil
)