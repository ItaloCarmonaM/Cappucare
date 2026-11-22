package com.example.cappucare.data.remote

import com.example.cappucare.HttpFalso
import com.example.cappucare.viewmodel.MensajesApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// llamarApi convierte las excepciones de red en Result.failure(ApiException)
class ManejoErroresTest : StringSpec({

    fun errorDe(resultado: Result<*>): ApiException = resultado.exceptionOrNull() as ApiException

    fun httpConCuerpo(codigo: Int, texto: String, tipo: String): HttpException =
        HttpException(Response.error<Any>(codigo, texto.toResponseBody(tipo.toMediaType())))

    "una llamada exitosa devuelve success con el valor" {
        val resultado = llamarApi { 42 }

        resultado.isSuccess shouldBe true
        resultado.getOrNull() shouldBe 42
    }

    "un error HTTP conserva el código y el mensaje que envía el servidor" {
        val resultado = llamarApi<Int> { throw HttpFalso.error(404, "El producto no existe") }

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 404
        errorDe(resultado).message shouldBe "El producto no existe"
    }

    "un error HTTP con cuerpo que no es JSON usa el mensaje genérico" {
        val resultado = llamarApi<Int> { throw httpConCuerpo(500, "<html>error</html>", "text/html") }

        errorDe(resultado).codigo shouldBe 500
        errorDe(resultado).message shouldBe "Error del servidor (500)"
    }

    "un error HTTP con JSON sin mensaje usa el mensaje genérico" {
        val resultado = llamarApi<Int> { throw httpConCuerpo(503, """{"otro":"dato"}""", "application/json") }

        errorDe(resultado).codigo shouldBe 503
        errorDe(resultado).message shouldBe "Error del servidor (503)"
    }

    "un IOException se informa como falta de conexión con código 0" {
        val resultado = llamarApi<Int> { throw IOException("sin red") }

        errorDe(resultado).codigo shouldBe 0
        errorDe(resultado).message shouldBe "No se pudo conectar con el servidor"
    }

    "MensajesApi entrega el mensaje de un ApiException" {
        MensajesApi.de(ApiException(409, "Este código ya está en uso")) shouldBe "Este código ya está en uso"
    }

    "MensajesApi usa un mensaje genérico para cualquier otro error" {
        MensajesApi.de(IllegalStateException("fallo interno")) shouldBe "Ocurrió un error inesperado"
    }

    "MensajesApi usa el mensaje genérico si el ApiException no trae mensaje" {
        MensajesApi.de(ApiException(500, "")) shouldBe "Ocurrió un error inesperado"
    }
})
