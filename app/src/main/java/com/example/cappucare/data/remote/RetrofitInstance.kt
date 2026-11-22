package com.example.cappucare.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton con la configuración de Retrofit para los dos microservicios
object RetrofitInstance {

    // 10.0.2.2 es el computador visto desde el emulador de Android
    private const val URL_USUARIOS = "http://10.0.2.2:8081/"
    private const val URL_INVENTARIO = "http://10.0.2.2:8082/"

    // Crea un servicio para la URL indicada (también lo usará la API externa)
    fun <T> crearServicio(urlBase: String, servicio: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(servicio)

    val usuariosApi: UsuariosApiService by lazy {
        crearServicio(URL_USUARIOS, UsuariosApiService::class.java)
    }

    val inventarioApi: InventarioApiService by lazy {
        crearServicio(URL_INVENTARIO, InventarioApiService::class.java)
    }
}