package com.example.cappucare.viewmodel

import com.example.cappucare.viewmodel.validacion.Validadores
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidadoresTest : StringSpec({

    "correo vacío es obligatorio" {
        Validadores.correo("") shouldBe "El correo es obligatorio"
        Validadores.correo("   ") shouldBe "El correo es obligatorio"
    }

    "correo sin formato válido se rechaza" {
        Validadores.correo("encargado") shouldBe "Ingresa un correo válido"
        Validadores.correo("encargado@") shouldBe "Ingresa un correo válido"
        Validadores.correo("encargado@cappucare") shouldBe "Ingresa un correo válido"
    }

    "correo válido no tiene error, aunque tenga espacios alrededor" {
        Validadores.correo("encargado@cappucare.cl") shouldBe null
        Validadores.correo("  encargado@cappucare.cl  ") shouldBe null
    }

    "clave vacía es obligatoria" {
        Validadores.clave("") shouldBe "La contraseña es obligatoria"
    }

    "clave de menos de 6 caracteres se rechaza" {
        Validadores.clave("12345") shouldBe "Debe tener al menos 6 caracteres"
    }

    "clave de 6 o más caracteres es válida" {
        Validadores.clave("123456") shouldBe null
        Validadores.clave("cappu123") shouldBe null
    }

    "obligatorio rechaza vacío y espacios, e indica el campo" {
        Validadores.obligatorio("", "el código") shouldBe "Ingresa el código"
        Validadores.obligatorio("   ", "el nombre") shouldBe "Ingresa el nombre"
        Validadores.obligatorio("MED-001", "el código") shouldBe null
    }

    "enteroNoNegativo acepta 0 y positivos (RN2)" {
        Validadores.enteroNoNegativo("0", "la cantidad") shouldBe null
        Validadores.enteroNoNegativo(" 15 ", "la cantidad") shouldBe null
    }

    "enteroNoNegativo rechaza negativos, vacío y no numéricos" {
        Validadores.enteroNoNegativo("-1", "la cantidad") shouldBe "No puede ser menor a 0"
        Validadores.enteroNoNegativo("", "la cantidad") shouldBe "Ingresa la cantidad"
        Validadores.enteroNoNegativo("abc", "la cantidad") shouldBe "Ingresa un número entero"
        Validadores.enteroNoNegativo("2.5", "la cantidad") shouldBe "Ingresa un número entero"
    }

    "enteroPositivo rechaza 0 y negativos (RN4)" {
        Validadores.enteroPositivo("0", "la cantidad") shouldBe "Debe ser mayor a 0"
        Validadores.enteroPositivo("-3", "la cantidad") shouldBe "Debe ser mayor a 0"
        Validadores.enteroPositivo("1", "la cantidad") shouldBe null
    }

    "enteroPositivo rechaza vacío y no numéricos" {
        Validadores.enteroPositivo("", "la cantidad") shouldBe "Ingresa la cantidad"
        Validadores.enteroPositivo("x", "la cantidad") shouldBe "Ingresa un número entero"
    }
})
