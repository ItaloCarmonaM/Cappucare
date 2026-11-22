package com.example.cappucare.data.remote

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

// Respuesta de error de los microservicios: { "mensaje": "..." }
data class ErrorResponse(val mensaje: String? = null)

// Error de la API con su código HTTP (0 = sin conexión con el servidor)
class ApiException(val codigo: Int, mensaje: String) : Exception(mensaje)

// Ejecuta una llamada a la API y convierte los errores en Result.failure
suspend fun <T> llamarApi(llamada: suspend () -> T): Result<T> {
    return try {
        Result.success(llamada())
    } catch (e: HttpException) {
        Result.failure(ApiException(e.code(), leerMensaje(e)))
    } catch (e: IOException) {
        Result.failure(ApiException(0, "No se pudo conectar con el servidor"))
    }
}

// Lee el mensaje del cuerpo del error; si no viene, usa uno genérico
private fun leerMensaje(e: HttpException): String {
    val mensaje = try {
        val cuerpo = e.response()?.errorBody()?.string()
        Gson().fromJson(cuerpo, ErrorResponse::class.java)?.mensaje
    } catch (ex: Exception) {
        null
    }
    return mensaje ?: "Error del servidor (${e.code()})"
}