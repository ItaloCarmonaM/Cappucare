package com.example.cappucare
import android.app.Application
import com.example.cappucare.data.remote.RetrofitInstance
import com.example.cappucare.notification.Notificaciones
import com.example.cappucare.repository.MovimientoRepository
import com.example.cappucare.repository.ProductoRepository
import com.example.cappucare.repository.UsuarioRepository
import com.example.cappucare.data.remote.WikipediaApiService
import com.example.cappucare.repository.WikipediaRepository

// Clase que se crea al iniciar la app: guarda los repositorios que se conectan a los microservicios
class CappucareApp : Application() {

    val usuarioRepository by lazy { UsuarioRepository(RetrofitInstance.usuariosApi) }
    val productoRepository by lazy { ProductoRepository(RetrofitInstance.inventarioApi) }
    val movimientoRepository by lazy { MovimientoRepository(RetrofitInstance.inventarioApi) }
    // API externa (Wikipedia): usa la misma fábrica de Retrofit con otra URL base
    val wikipediaRepository by lazy {
        WikipediaRepository(
            RetrofitInstance.crearServicio(WikipediaApiService.URL_BASE, WikipediaApiService::class.java)
        )
    }

    override fun onCreate() {
        super.onCreate()
        // El canal de notificaciones se crea una sola vez al abrir la app
        Notificaciones.crearCanal(this)
    }
}