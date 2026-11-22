package com.example.cappucare.repository

import com.example.cappucare.HttpFalso
import com.example.cappucare.data.remote.ApiException
import com.example.cappucare.data.remote.WikipediaApiService
import com.example.cappucare.model.WikipediaResumen
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException

class WikipediaRepositoryTest : StringSpec({

    val termometro = WikipediaResumen(
        type = "standard", title = "Termómetro", description = "Instrumento de medición",
        extract = "Un termómetro es un instrumento que mide la temperatura."
    )

    "devuelve el resumen cuando existe el artículo del nombre completo" {
        val api = mockk<WikipediaApiService>()
        coEvery { api.obtenerResumen("Termómetro digital") } returns termometro

        val resultado = WikipediaRepository(api).buscarResumen("termómetro digital")

        resultado.getOrNull() shouldBe termometro
    }

    "si no existe el nombre completo prueba con la primera palabra" {
        val api = mockk<WikipediaApiService>()
        coEvery { api.obtenerResumen("Termómetro digital") } throws HttpFalso.error(404, "no existe")
        coEvery { api.obtenerResumen("Termómetro") } returns termometro

        val resultado = WikipediaRepository(api).buscarResumen("Termómetro digital")

        resultado.getOrNull() shouldBe termometro
    }

    "si ningún título existe devuelve éxito sin resumen (Sin información)" {
        val api = mockk<WikipediaApiService>()
        coEvery { api.obtenerResumen(any()) } throws HttpFalso.error(404, "no existe")

        val resultado = WikipediaRepository(api).buscarResumen("Producto inventado")

        resultado.isSuccess shouldBe true
        resultado.getOrNull() shouldBe null
    }

    "una página de desambiguación se trata como sin información" {
        val api = mockk<WikipediaApiService>()
        coEvery { api.obtenerResumen(any()) } returns WikipediaResumen(type = "disambiguation", extract = "Puede referirse a:")

        val resultado = WikipediaRepository(api).buscarResumen("Mascarilla")

        resultado.getOrNull() shouldBe null
    }

    "sin conexión devuelve error y no sigue probando títulos" {
        val api = mockk<WikipediaApiService>()
        coEvery { api.obtenerResumen(any()) } throws IOException("sin red")

        val resultado = WikipediaRepository(api).buscarResumen("Termómetro digital")

        resultado.isFailure shouldBe true
        resultado.exceptionOrNull().shouldBeInstanceOf<ApiException>()
        coVerify(exactly = 1) { api.obtenerResumen(any()) }
    }
})