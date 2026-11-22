package com.example.cappucare.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// RN7: Agotado si la cantidad es 0, Stock bajo si es menor o igual al mínimo, Disponible en los demás casos
class ProductoEstadoTest : StringSpec({

    fun producto(cantidad: Int, nivelMinimo: Int) = Producto(
        id = 1, codigo = "MED-001", nombre = "Guantes", descripcion = "", categoria = "Protección",
        cantidad = cantidad, unidadMedida = "caja", nivelMinimo = nivelMinimo, ubicacion = "Bodega A"
    )

    "cantidad 0 es AGOTADO" {
        producto(cantidad = 0, nivelMinimo = 10).estado shouldBe EstadoProducto.AGOTADO
    }

    "cantidad 0 es AGOTADO aunque el nivel mínimo sea 0" {
        producto(cantidad = 0, nivelMinimo = 0).estado shouldBe EstadoProducto.AGOTADO
    }

    "cantidad bajo el mínimo es STOCK_BAJO" {
        producto(cantidad = 5, nivelMinimo = 10).estado shouldBe EstadoProducto.STOCK_BAJO
    }

    "cantidad igual al mínimo es STOCK_BAJO" {
        producto(cantidad = 10, nivelMinimo = 10).estado shouldBe EstadoProducto.STOCK_BAJO
    }

    "cantidad sobre el mínimo es DISPONIBLE" {
        producto(cantidad = 11, nivelMinimo = 10).estado shouldBe EstadoProducto.DISPONIBLE
    }

    "con mínimo 0, cualquier cantidad positiva es DISPONIBLE" {
        producto(cantidad = 1, nivelMinimo = 0).estado shouldBe EstadoProducto.DISPONIBLE
    }
})
