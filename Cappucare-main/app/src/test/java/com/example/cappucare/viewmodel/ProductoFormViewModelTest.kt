package com.example.cappucare.viewmodel

import com.example.cappucare.HttpFalso
import com.example.cappucare.data.remote.InventarioApiService
import com.example.cappucare.model.Producto
import com.example.cappucare.repository.ProductoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoFormViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    val existente = Producto(
        id = 7, codigo = "MED-007", nombre = "Mascarilla", descripcion = "Caja de 50",
        categoria = "Protección", cantidad = 40, unidadMedida = "caja", nivelMinimo = 10,
        ubicacion = "Bodega B"
    )

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun crear(api: InventarioApiService, productoId: Int = 0) =
        ProductoFormViewModel(ProductoRepository(api), productoId)

    fun avanzar() = dispatcher.scheduler.advanceUntilIdle()

    // Completa el formulario de un producto nuevo con datos válidos
    fun ProductoFormViewModel.llenarValido() {
        onCodigoChange("MED-100")
        onNombreChange("  Alcohol gel  ")
        onDescripcionChange("Botella 500 ml")
        onCategoriaChange("Higiene")
        onCantidadChange("25")
        onUnidadMedidaChange("unidad")
        onNivelMinimoChange("5")
        onUbicacionChange("Bodega A")
    }

    "formulario vacío marca todos los campos obligatorios" {
        val vm = crear(mockk())

        vm.validarFormulario() shouldBe false

        val e = vm.estado.value.errores
        e.codigo shouldBe "Ingresa el código"
        e.nombre shouldBe "Ingresa el nombre"
        e.categoria shouldBe "Ingresa la categoría"
        e.cantidad shouldBe "Ingresa la cantidad"
        e.unidadMedida shouldBe "Ingresa la unidad de medida"
        e.nivelMinimo shouldBe "Ingresa el nivel mínimo"
        e.ubicacion shouldBe "Ingresa la ubicación"
    }

    "la descripción es opcional" {
        val vm = crear(mockk())
        vm.llenarValido()
        vm.onDescripcionChange("")

        vm.validarFormulario() shouldBe true
    }

    "cantidad negativa se rechaza (RN2)" {
        val vm = crear(mockk())
        vm.llenarValido()
        vm.onCantidadChange("-5")

        vm.validarFormulario() shouldBe false
        vm.estado.value.errores.cantidad shouldBe "No puede ser menor a 0"
    }

    "escribir en un campo limpia su error" {
        val vm = crear(mockk())
        vm.validarFormulario()

        vm.onCodigoChange("MED-1")

        vm.estado.value.errores.codigo shouldBe null
        vm.estado.value.errores.nombre shouldBe "Ingresa el nombre"
    }

    "guardar con datos inválidos no llama al servidor" {
        val api = mockk<InventarioApiService>()
        val vm = crear(api)
        var guardado = false

        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe false
        coVerify(exactly = 0) { api.crearProducto(any()) }
    }

    "crear un producto envía los datos limpios y avisa al terminar" {
        val api = mockk<InventarioApiService>()
        val enviado = slot<Producto>()
        coEvery { api.crearProducto(capture(enviado)) } answers { enviado.captured.copy(id = 99) }
        val vm = crear(api)
        vm.llenarValido()
        var guardado = false

        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe true
        vm.estado.value.guardando shouldBe false
        enviado.captured.nombre shouldBe "Alcohol gel"
        enviado.captured.cantidad shouldBe 25
        enviado.captured.nivelMinimo shouldBe 5
        enviado.captured.codigo shouldBe "MED-100"
    }

    "código repetido (409) marca el campo código y no cierra el formulario (RN1)" {
        val api = mockk<InventarioApiService>()
        coEvery { api.crearProducto(any()) } throws HttpFalso.error(409, "El código ya existe")
        val vm = crear(api)
        vm.llenarValido()
        var guardado = false

        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe false
        vm.estado.value.guardando shouldBe false
        vm.estado.value.errores.codigo shouldBe "Este código ya está en uso"
        vm.estado.value.errorGeneral shouldBe null
    }

    "sin conexión muestra un error general y permite reintentar" {
        val api = mockk<InventarioApiService>()
        coEvery { api.crearProducto(any()) } throws IOException("sin red")
        val vm = crear(api)
        vm.llenarValido()
        var guardado = false

        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe false
        vm.estado.value.guardando shouldBe false
        vm.estado.value.errorGeneral shouldBe "No se pudo conectar con el servidor"

        // El usuario reintenta y esta vez funciona
        coEvery { api.crearProducto(any()) } returns existente
        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe true
        vm.estado.value.errorGeneral shouldBe null
    }

    "en edición carga los datos del producto en el formulario" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProducto(7) } returns existente

        val vm = crear(api, productoId = 7)
        vm.estado.value.esEdicion shouldBe true
        vm.estado.value.cargando shouldBe true
        avanzar()

        val e = vm.estado.value
        e.cargando shouldBe false
        e.codigo shouldBe "MED-007"
        e.nombre shouldBe "Mascarilla"
        e.cantidad shouldBe "40"
        e.nivelMinimo shouldBe "10"
        e.ubicacion shouldBe "Bodega B"
    }

    "si falla la carga del producto informa el error y permite reintentar" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProducto(7) } throws IOException("sin red")
        val vm = crear(api, productoId = 7)
        avanzar()

        vm.estado.value.errorCarga shouldBe "No se pudo conectar con el servidor"
        vm.estado.value.cargando shouldBe false

        coEvery { api.obtenerProducto(7) } returns existente
        vm.cargarProducto()
        avanzar()

        vm.estado.value.errorCarga shouldBe null
        vm.estado.value.codigo shouldBe "MED-007"
    }

    "no se puede guardar una edición mientras el producto no se ha cargado" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProducto(7) } throws IOException("sin red")
        val vm = crear(api, productoId = 7)
        avanzar()
        var guardado = false

        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe false
        coVerify(exactly = 0) { api.actualizarProducto(any(), any()) }
    }

    "editar conserva el stock original (RN6) y actualiza el resto de los datos" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerProducto(7) } returns existente
        val enviado = slot<Producto>()
        coEvery { api.actualizarProducto(7, capture(enviado)) } answers { enviado.captured }
        val vm = crear(api, productoId = 7)
        avanzar()
        vm.onNombreChange("Mascarilla quirúrgica")
        vm.onNivelMinimoChange("15")
        var guardado = false

        vm.guardar { guardado = true }
        avanzar()

        guardado shouldBe true
        enviado.captured.id shouldBe 7
        enviado.captured.nombre shouldBe "Mascarilla quirúrgica"
        enviado.captured.nivelMinimo shouldBe 15
        enviado.captured.cantidad shouldBe 40
    }
})
