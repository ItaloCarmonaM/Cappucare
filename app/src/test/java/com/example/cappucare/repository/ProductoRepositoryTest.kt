package com.example.cappucare.repository

import com.example.cappucare.HttpFalso
import com.example.cappucare.data.remote.ApiException
import com.example.cappucare.data.remote.InventarioApiService
import com.example.cappucare.model.Producto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException

class ProductoRepositoryTest : StringSpec({

    val guantes = Producto(
        id = 1, codigo = "MED-001", nombre = "Guantes de nitrilo", descripcion = "Caja de 100",
        categoria = "Protección", cantidad = 20, unidadMedida = "caja", nivelMinimo = 5, ubicacion = "Bodega A"
    )
    val termometro = Producto(
        id = 2, codigo = "MED-006", nombre = "Termómetro digital", descripcion = "",
        categoria = "Equipos", cantidad = 3, unidadMedida = "unidad", nivelMinimo = 10, ubicacion = "Bodega B"
    )

    // El repositorio siempre entrega un ApiException cuando falla
    fun errorDe(resultado: Result<*>): ApiException = resultado.exceptionOrNull() as ApiException

    "obtenerTodos devuelve los productos del servidor" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } returns listOf(guantes, termometro)

        val resultado = ProductoRepository(api).obtenerTodos()

        resultado.isSuccess shouldBe true
        resultado.getOrNull() shouldBe listOf(guantes, termometro)
    }

    "obtenerTodos sin conexión devuelve error con código 0" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } throws IOException("sin red")

        val resultado = ProductoRepository(api).obtenerTodos()

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 0
        errorDe(resultado).message shouldBe "No se pudo conectar con el servidor"
    }

    "buscar envía el texto sin espacios al inicio y al final" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos("termómetro") } returns listOf(termometro)

        val resultado = ProductoRepository(api).buscar("  termómetro ")

        resultado.getOrNull() shouldBe listOf(termometro)
        coVerify(exactly = 1) { api.obtenerProductos("termómetro") }
    }

    "obtenerStockBajo devuelve los productos en alerta" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerStockBajo() } returns listOf(termometro)

        val resultado = ProductoRepository(api).obtenerStockBajo()

        resultado.getOrNull() shouldBe listOf(termometro)
    }

    "obtenerPorId devuelve el producto pedido" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProducto(1) } returns guantes

        val resultado = ProductoRepository(api).obtenerPorId(1)

        resultado.getOrNull() shouldBe guantes
    }

    "obtenerPorId con un producto inexistente informa el error 404 del servidor" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProducto(99) } throws HttpFalso.error(404, "El producto no existe")

        val resultado = ProductoRepository(api).obtenerPorId(99)

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 404
        errorDe(resultado).message shouldBe "El producto no existe"
    }

    "crear envía el producto y devuelve el que guardó el servidor" {
        val api = mockk<InventarioApiService>()
        val nuevo = guantes.copy(id = 0)
        coEvery { api.crearProducto(nuevo) } returns guantes

        val resultado = ProductoRepository(api).crear(nuevo)

        resultado.getOrNull() shouldBe guantes
    }

    "crear con un código repetido informa el error 409 (RN1)" {
        val api = mockk<InventarioApiService>()
        coEvery { api.crearProducto(any()) } throws HttpFalso.error(409, "Este código ya está en uso")

        val resultado = ProductoRepository(api).crear(guantes.copy(id = 0))

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 409
        errorDe(resultado).message shouldBe "Este código ya está en uso"
    }

    "actualizar usa el id del producto en la ruta" {
        val api = mockk<InventarioApiService>()
        val editado = guantes.copy(nombre = "Guantes de nitrilo talla L")
        coEvery { api.actualizarProducto(1, editado) } returns editado

        val resultado = ProductoRepository(api).actualizar(editado)

        resultado.getOrNull() shouldBe editado
        coVerify(exactly = 1) { api.actualizarProducto(1, editado) }
    }

    "un error 500 muestra el mensaje que envía el servidor" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerStockBajo() } throws HttpFalso.error(500, "Error interno")

        val resultado = ProductoRepository(api).obtenerStockBajo()

        errorDe(resultado).codigo shouldBe 500
        errorDe(resultado).message shouldBe "Error interno"
    }
})
