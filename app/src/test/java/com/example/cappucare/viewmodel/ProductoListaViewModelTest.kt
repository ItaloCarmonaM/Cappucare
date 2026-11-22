package com.example.cappucare.viewmodel

import com.example.cappucare.data.remote.InventarioApiService
import com.example.cappucare.model.Producto
import com.example.cappucare.repository.ProductoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoListaViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    fun producto(id: Int, nombre: String) = Producto(
        id = id, codigo = "MED-00$id", nombre = nombre, descripcion = "", categoria = "Protección",
        cantidad = 20, unidadMedida = "caja", nivelMinimo = 5, ubicacion = "Bodega A"
    )

    val guantes = producto(1, "Guantes de nitrilo")
    val termometro = producto(2, "Termómetro digital")

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun crear(api: InventarioApiService) = ProductoListaViewModel(ProductoRepository(api))
    fun avanzar() = dispatcher.scheduler.advanceUntilIdle()

    "al crearse no consulta hasta que la pantalla llama a cargar" {
        val api = mockk<InventarioApiService>()
        val vm = crear(api)
        avanzar()

        vm.estado.value.productos shouldBe emptyList()
        coVerify(exactly = 0) { api.obtenerProductos(any()) }
    }

    "cargar muestra los productos del servidor" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } returns listOf(guantes, termometro)
        val vm = crear(api)

        vm.cargar()
        avanzar()

        vm.estado.value.productos shouldBe listOf(guantes, termometro)
        vm.estado.value.cargando shouldBe false
        vm.estado.value.error shouldBe null
    }

    "cargar muestra el estado de carga mientras espera al servidor" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } coAnswers {
            delay(1_000)
            listOf(guantes)
        }
        val vm = crear(api)

        vm.cargar()
        dispatcher.scheduler.runCurrent()
        vm.estado.value.cargando shouldBe true

        avanzar()
        vm.estado.value.cargando shouldBe false
        vm.estado.value.productos shouldBe listOf(guantes)
    }

    "sin conexión informa el error y no hay productos" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } throws IOException("sin red")
        val vm = crear(api)

        vm.cargar()
        avanzar()

        vm.estado.value.error shouldBe "No se pudo conectar con el servidor"
        vm.estado.value.productos shouldBe emptyList()
        vm.estado.value.cargando shouldBe false
    }

    "un error del servidor muestra su mensaje" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } throws HttpFalso.error(500, "Error interno")
        val vm = crear(api)

        vm.cargar()
        avanzar()

        vm.estado.value.error shouldBe "Error interno"
    }

    "reintentar después de un error carga los productos y limpia el error" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } throws IOException("sin red")
        val vm = crear(api)
        vm.cargar()
        avanzar()
        vm.estado.value.error shouldBe "No se pudo conectar con el servidor"

        coEvery { api.obtenerProductos(null) } returns listOf(guantes)
        vm.cargar()
        avanzar()

        vm.estado.value.error shouldBe null
        vm.estado.value.productos shouldBe listOf(guantes)
    }

    "si falla una actualización se conservan los productos ya mostrados" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos(null) } returns listOf(guantes)
        val vm = crear(api)
        vm.cargar()
        avanzar()

        coEvery { api.obtenerProductos(null) } throws IOException("sin red")
        vm.cargar()
        avanzar()

        vm.estado.value.productos shouldBe listOf(guantes)
        vm.estado.value.error shouldBe "No se pudo conectar con el servidor"
    }

    "la búsqueda espera una pausa antes de consultar al servidor" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos("ter") } returns listOf(termometro)
        val vm = crear(api)

        vm.onBusquedaChange("ter")
        dispatcher.scheduler.advanceTimeBy(100)
        dispatcher.scheduler.runCurrent()
        coVerify(exactly = 0) { api.obtenerProductos("ter") }

        avanzar()
        coVerify(exactly = 1) { api.obtenerProductos("ter") }
        vm.estado.value.busqueda shouldBe "ter"
        vm.estado.value.productos shouldBe listOf(termometro)
    }

    "al escribir seguido solo se consulta el último texto" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos("termo") } returns listOf(termometro)
        val vm = crear(api)

        vm.onBusquedaChange("t")
        dispatcher.scheduler.advanceTimeBy(100)
        vm.onBusquedaChange("te")
        dispatcher.scheduler.advanceTimeBy(100)
        vm.onBusquedaChange("termo")
        avanzar()

        coVerify(exactly = 1) { api.obtenerProductos("termo") }
        coVerify(exactly = 0) { api.obtenerProductos("t") }
        coVerify(exactly = 0) { api.obtenerProductos("te") }
    }

    "la búsqueda se envía sin espacios al inicio y al final" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos("guantes") } returns listOf(guantes)
        val vm = crear(api)

        vm.onBusquedaChange("  guantes ")
        avanzar()

        coVerify(exactly = 1) { api.obtenerProductos("guantes") }
        vm.estado.value.productos shouldBe listOf(guantes)
    }

    "limpiar la búsqueda vuelve a listar todos los productos" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProductos("ter") } returns listOf(termometro)
        coEvery { api.obtenerProductos(null) } returns listOf(guantes, termometro)
        val vm = crear(api)
        vm.onBusquedaChange("ter")
        avanzar()

        vm.onBusquedaChange("")
        avanzar()

        vm.estado.value.busqueda shouldBe ""
        vm.estado.value.productos shouldBe listOf(guantes, termometro)
    }
})
