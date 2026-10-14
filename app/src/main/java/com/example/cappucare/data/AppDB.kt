package com.example.cappucare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cappucare.model.Movimiento
import com.example.cappucare.model.Producto
import com.example.cappucare.model.Usuario

// Base de datos local de la aplicación
@Database(
    entities = [Usuario::class, Producto::class, Movimiento::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        // Única instancia de la base de datos (Singleton)
        @Volatile
        private var INSTANCIA: AppDB? = null

        fun obtenerInstancia(context: Context): AppDB {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "cappustock_db"
                ).build().also { INSTANCIA = it }
            }
        }
    }
}