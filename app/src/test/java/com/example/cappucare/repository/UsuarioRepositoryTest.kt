package com.example.cappucare.repository

import com.example.cappucare.HttpFalso
import com.example.cappucare.data.remote.ApiException
import com.example.cappucare.data.remote.LoginRequest
import com.example.cappucare.data.remote.UsuariosApiService
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Usuario
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException

class UsuarioRepositoryTest : StringSpec({

    val encargado = Usuario(1, "María González", "encargado@cappucare.cl", Perfil.ENCARGADO)

    fun errorDe(resultado: Result<*>): ApiException = resultado.exceptionOrNull() as ApiException

    "autenticar con credenciales correctas devuelve el usuario" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(LoginRequest("encargado@cappucare.cl", "cappu123")) } returns encargado

        val resultado = UsuarioRepository(api).autenticar("encargado@cappucare.cl", "cappu123")

        resultado.isSuccess shouldBe true
        resultado.getOrNull() shouldBe encargado
    }

    "autenticar quita los espacios del correo pero no toca la clave" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(LoginRequest("encargado@cappucare.cl", " cappu123 ")) } returns encargado

        val resultado = UsuarioRepository(api).autenticar("  encargado@cappucare.cl ", " cappu123 ")

        resultado.getOrNull() shouldBe encargado
    }

    "autenticar con credenciales incorrectas informa el error 401" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } throws HttpFalso.error(401, "Correo o contraseña incorrectos")

        val resultado = UsuarioRepository(api).autenticar("encargado@cappucare.cl", "mala-clave")

        resultado.isFailure shouldBe true
        errorDe(resultado).codigo shouldBe 401
        errorDe(resultado).message shouldBe "Correo o contraseña incorrectos"
    }

    "autenticar sin conexión devuelve error con código 0" {
        val api = mockk<UsuariosApiService>()
        coEvery { api.login(any()) } throws IOException("timeout")

        val resultado = UsuarioRepository(api).autenticar("encargado@cappucare.cl", "cappu123")

        errorDe(resultado).codigo shouldBe 0
        errorDe(resultado).message shouldBe "No se pudo conectar con el servidor"
    }
})
