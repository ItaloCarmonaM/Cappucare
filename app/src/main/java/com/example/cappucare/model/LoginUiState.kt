package com.example.cappucare.model

// Estado de la pantalla de login
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val ingresoExitoso: Boolean = false
)