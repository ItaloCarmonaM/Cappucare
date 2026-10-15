package com.example.cappucare

import android.app.Application
import com.example.cappucare.data.AppDB
import com.example.cappucare.data.DatosIniciales
import com.example.cappucare.repository.MovimientoRepository
import com.example.cappucare.repository.ProductoRepository
import com.example.cappucare.repository.UsuarioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// Clase que se crea al iniciar la app: guarda la base de datos y los repositorios
class CappucareApp : Application() {

    val database by lazy { AppDB.obtenerInstancia(this) }
    val usuarioRepository by lazy { UsuarioRepository(database.usuarioDao()) }
    val productoRepository by lazy { ProductoRepository(database.productoDao()) }
    val movimientoRepository by lazy { MovimientoRepository(database) }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Carga los datos de prueba en segundo plano (solo la primera vez)
        scope.launch { DatosIniciales.cargar(database) }
    }
}