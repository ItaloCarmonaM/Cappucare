package com.example.cappucare.viewmodel

import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Guarda el usuario que inició sesión (solo en memoria)
object Sesion {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    // Permite que la UI reaccione cuando alguien inicia o cierra sesión
    val usuarioFlow: StateFlow<Usuario?> = _usuario.asStateFlow()

    val usuario: Usuario?
        get() = _usuario.value

    val esEncargado: Boolean
        get() = usuario?.perfil == Perfil.ENCARGADO

    fun iniciar(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrar() {
        _usuario.value = null
    }
}