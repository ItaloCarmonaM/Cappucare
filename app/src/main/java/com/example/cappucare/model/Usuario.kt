package com.example.cappucare.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Usuario simulado para el login (datos ficticios)
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil
)