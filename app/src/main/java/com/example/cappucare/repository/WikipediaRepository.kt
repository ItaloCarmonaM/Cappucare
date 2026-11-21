package com.example.cappucare.repository

import com.example.cappucare.data.remote.ApiException
import com.example.cappucare.data.remote.WikipediaApiService
import com.example.cappucare.data.remote.llamarApi
import com.example.cappucare.model.WikipediaResumen

// Acceso a la API externa de Wikipedia
class WikipediaRepository(private val api: WikipediaApiService) {

    // Busca el artículo del producto. Resultados posibles:
    //  - success(resumen): se encontró un artículo
    //  - success(null):    no hay artículo ("Sin información disponible")
    //  - failure:          no se pudo consultar (sin conexión, error del servidor)
    suspend fun buscarResumen(nombreProducto: String): Result<WikipediaResumen?> {
        for (titulo in candidatos(nombreProducto)) {
            val resultado = llamarApi { api.obtenerResumen(titulo) }
            val resumen = resultado.getOrNull()
            if (resumen != null) {
                if (resumen.tieneInformacion) return Result.success(resumen)
                continue
            }
            val error = resultado.exceptionOrNull()
            // 404 = el artículo no existe: se prueba el siguiente título. Otro error se informa
            if (!(error is ApiException && error.codigo == 404)) {
                return Result.failure(error ?: IllegalStateException("Respuesta vacía"))
            }
        }
        return Result.success(null)
    }

    // Primero el nombre completo ("Termómetro digital"); si no existe, su primera palabra ("Termómetro")
    internal fun candidatos(nombre: String): List<String> {
        val completo = nombre.trim().replaceFirstChar { it.uppercase() }
        if (completo.isBlank()) return emptyList()
        val primera = completo.substringBefore(' ')
        return if (primera.length >= 4 && primera != completo) listOf(completo, primera) else listOf(completo)
    }
}
