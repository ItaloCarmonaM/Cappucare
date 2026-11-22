package com.example.cappucare.viewmodel

import com.example.cappucare.data.remote.LoginRequest
import com.example.cappucare.data.remote.UsuariosApiService
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Usuario
import com.example.cappucare.repository.UsuarioRepository
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
class LoginViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()
    val encargado = Usuario(1, "María González", "encargado@cappucare.cl", Perfil.ENCARGADO)

    beforeTest {
        Dispatchers.setMain(dispatcher)
        Sesion.cerrar()
    }
    afterTest {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    fun crear(api: UsuariosApiService) = LoginViewModel(UsuarioRepository(api))
    fun avanzar() = dispatcher.scheduler.advanceUntilIdle()

    "con campos vacíos muestra errores y no llama al servidor" {
        val api = mockk<UsuariosApiService>()
        val vm = crear(api)

        vm.ingresar()
        avanzar()

        vm.estado.value.errorCorreo shouldBe "El correo es obligatorio"
        vm.estado.value.errorClave shouldBe "La contraseña es obligatoria"
        coVerify(exactly = 0) { api.login(any()) }
    }

    "con credenciales correctas inicia la sesión" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(LoginRequest("encargado@cappucare.cl", "cappu123")) } returns encargado
        val vm = crear(api)

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.onClaveChange("cappu123")
        vm.ingresar()
        avanzar()

        vm.estado.value.ingresoExitoso shouldBe true
        vm.estado.value.cargando shouldBe false
        vm.estado.value.errorGeneral shouldBe null
        Sesion.usuario shouldBe encargado
    }

    "muestra el indicador de carga mientras espera la respuesta" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } coAnswers {
            delay(1_000)
            encargado
        }
        val vm = crear(api)

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.onClaveChange("cappu123")
        vm.ingresar()
        dispatcher.scheduler.runCurrent()
        vm.estado.value.cargando shouldBe true

        avanzar()
        vm.estado.value.cargando shouldBe false
    }

    "con error 401 informa credenciales incorrectas y no inicia sesión" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } throws HttpFalso.error(401, "Credenciales inválidas")
        val vm = crear(api)

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.onClaveChange("mala-clave")
        vm.ingresar()
        avanzar()

        vm.estado.value.errorGeneral shouldBe "Correo o contraseña incorrectos"
        vm.estado.value.ingresoExitoso shouldBe false
        vm.estado.value.cargando shouldBe false
        Sesion.usuario shouldBe null
    }

    "sin conexión informa que no se pudo conectar" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } throws IOException("timeout")
        val vm = crear(api)

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.onClaveChange("cappu123")
        vm.ingresar()
        avanzar()

        vm.estado.value.errorGeneral shouldBe "No se pudo conectar con el servidor"
        vm.estado.value.ingresoExitoso shouldBe false
        vm.estado.value.cargando shouldBe false
    }

    "un error 500 muestra el mensaje del servidor" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } throws HttpFalso.error(500, "Error interno")
        val vm = crear(api)

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.onClaveChange("cappu123")
        vm.ingresar()
        avanzar()

        vm.estado.value.errorGeneral shouldBe "Error interno"
    }

    "al corregir un campo con error se revalida, y escribir limpia el error general" {
        val api = mockk<UsuariosApiService>()
        val vm = crear(api)

        vm.ingresar() // deja errores en ambos campos
        vm.onCorreoChange("mal")
        vm.estado.value.errorCorreo shouldBe "Ingresa un correo válido"

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.estado.value.errorCorreo shouldBe null
    }

    "el error general desaparece al volver a escribir" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } throws HttpFalso.error(401, "x")
        val vm = crear(api)

        vm.onCorreoChange("encargado@cappucare.cl")
        vm.onClaveChange("mala-clave")
        vm.ingresar()
        avanzar()
        vm.estado.value.errorGeneral shouldBe "Correo o contraseña incorrectos"

        vm.onClaveChange("otra-clave")
        vm.estado.value.errorGeneral shouldBe null
    }
})
