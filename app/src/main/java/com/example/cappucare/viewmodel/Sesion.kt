package com.example.cappucare.viewmodel

import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Usuario

// Guarda el usuario que inició sesión (simulado, solo en memoria).
// TEMPORAL: si el equipo define otra forma de manejar la sesión, se reemplaza este archivo.
object Sesion {
    var usuario: Usuario? = null
        private set

    val esEncargado: Boolean
        get() = usuario?.perfil == Perfil.ENCARGADO

    fun iniciar(usuario: Usuario) {
        this.usuario = usuario
    }

    fun cerrar() {
        usuario = null
    }
}
