package com.example.cappucare.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cappucare.model.Usuario

// Acceso a los datos de los usuarios simulados
@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    // Devuelve el usuario si el correo y la clave coinciden, o null si no existe
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun autenticar(correo: String, clave: String): Usuario?

    // Se usa para saber si ya se cargaron los datos de prueba
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}