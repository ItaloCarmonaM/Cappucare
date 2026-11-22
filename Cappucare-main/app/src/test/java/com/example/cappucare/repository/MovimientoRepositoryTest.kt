package com.example.cappucare.repository

import com.example.cappucare.HttpFalso
import com.example.cappucare.data.remote.ApiException
import com.example.cappucare.data.remote.InventarioApiService
import com.example.cappucare.data.remote.MovimientoRequest
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.Movimiento
import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Producto
import com.example.cappucare.model.ResultadoMovimiento
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.model.Usuario
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import java.io.IOException

class MovimientoRepositoryTest : StringSpec({

    val encargado = Usuario(1, "María González", "encargado@cappucare.cl", Perfil.ENCARGADO)

    val guantes = Producto(
        id = 5, codigo = "MED-001", nombre = "Guantes de nitrilo", descripcion = "Caja de 100",
        categoria = "Protección", cantidad = 20, unidadMedida = "caja", nivelMinimo = 5, ubicacion = "Bodega A"
    )
    val resultadoOk = ResultadoMovimiento(producto = guantes, alcanzoNivelMinimo = false)

    fun errorDe(resultado: Result<*>): ApiException = resultado.exceptionOrNull() as ApiException

    "obtenerHistorial devuelve los movimientos del servidor" {
        val api = mockk<InventarioApiService>()
        val detalle = MovimientoDetalle(
            movimiento = Movimiento(
                id = 1, productoId = 5, usuarioId = 1, tipo = TipoMovimiento.INGRESO,
                cantidad = 10, motivo = null, fecha = 1_700_000_000_000L
            ),
            productoNombre = "Guantes de nitrilo",
            usuarioNombre = "María González"
        )
        coEvery { api.obtenerMovimientos() } returns listOf(detalle)

        val resultado = MovimientoRepository(api).obtenerHistorial()

        resultado.getOrNull() shouldBe listOf(detalle)
    }

    "obtenerHistorial sin conexión devuelve error" {
        val api = mockk<InventarioApiService>()
        coEvery { api.obtenerMovimientos() } throws IOException("sin red")

        val resultado = MovimientoRepository(api).obtenerHistorial()

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 0
    }

    "registrar una salida envía el responsable, la cantidad y el motivo" {
        val api = mockk<InventarioApiService>()
        val enviado = slot<MovimientoRequest>()
        coEvery { api.registrarMovimiento(capture(enviado)) } returns resultadoOk

        val resultado = MovimientoRepository(api)
            .registrar(encargado, productoId = 5, tipo = TipoMovimiento.SALIDA, cantidad = 3, motivo = MotivoSalida.VENTA)

        resultado.getOrNull() shouldBe resultadoOk
        enviado.captured.productoId shouldBe 5
        enviado.captured.usuarioId shouldBe 1
        enviado.captured.usuarioNombre shouldBe "María González"
        enviado.captured.tipo shouldBe TipoMovimiento.SALIDA
        enviado.captured.cantidad shouldBe 3
        enviado.captured.motivo shouldBe MotivoSalida.VENTA
    }

    "registrar un ingreso no envía motivo aunque se le pase uno" {
        val api = mockk<InventarioApiService>()
        val enviado = slot<MovimientoRequest>()
        coEvery { api.registrarMovimiento(capture(enviado)) } returns resultadoOk

        MovimientoRepository(api)
            .registrar(encargado, productoId = 5, tipo = TipoMovimiento.INGRESO, cantidad = 10, motivo = MotivoSalida.VENTA)

        enviado.captured.tipo shouldBe TipoMovimiento.INGRESO
        enviado.captured.motivo shouldBe null
    }

    "registrar informa si el producto llegó a su nivel mínimo (RN8)" {
        val api = mockk<InventarioApiService>()
        coEvery { api.registrarMovimiento(any()) } returns resultadoOk.copy(alcanzoNivelMinimo = true)

        val resultado = MovimientoRepository(api)
            .registrar(encargado, 5, TipoMovimiento.SALIDA, 15, MotivoSalida.VENTA)

        resultado.getOrNull()?.alcanzoNivelMinimo shouldBe true
    }

    "registrar una salida mayor al stock informa el error 400 del servidor (RN3)" {
        val api = mockk<InventarioApiService>()
        coEvery { api.registrarMovimiento(any()) } throws HttpFalso.error(400, "Stock insuficiente: disponible 20")

        val resultado = MovimientoRepository(api)
            .registrar(encargado, 5, TipoMovimiento.SALIDA, 50, MotivoSalida.VENTA)

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 400
        errorDe(resultado).message shouldBe "Stock insuficiente: disponible 20"
    }

    "registrar sin conexión devuelve error" {
        val api = mockk<InventarioApiService>()
        coEvery { api.registrarMovimiento(any()) } throws IOException("sin red")

        val resultado = MovimientoRepository(api)
            .registrar(encargado, 5, TipoMovimiento.INGRESO, 10, null)

        errorDe(resultado).codigo shouldBe 0
        errorDe(resultado).message shouldBe "No se pudo conectar con el servidor"
    }
})
