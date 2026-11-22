package com.example.cappucare.data.remote

import com.example.cappucare.model.WikipediaResumen
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

// API externa: Wikipedia en español (REST API pública, sin clave)
interface WikipediaApiService {

    // Wikipedia pide identificar a la aplicación que consulta
    @Headers("User-Agent: Cappucare/1.0 (proyecto academico DSY1105)")
    @GET("api/rest_v1/page/summary/{titulo}")
    suspend fun obtenerResumen(@Path("titulo") titulo: String): WikipediaResumen

    companion object {
        const val URL_BASE = "https://es.wikipedia.org/"
    }
}
