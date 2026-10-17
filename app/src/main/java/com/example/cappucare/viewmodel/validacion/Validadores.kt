package com.example.cappucare.viewmodel.validacion

// Reglas de validación centralizadas: no dependen de la interfaz.
// Cada función devuelve null si el valor es correcto, o el mensaje de error.
object Validadores {

    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !REGEX_CORREO.matches(valor.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    fun clave(valor: String): String? = when {
        valor.isBlank() -> "La contraseña es obligatoria"
        valor.length < 6 -> "Debe tener al menos 6 caracteres"
        else -> null
    }

    fun obligatorio(valor: String, campo: String): String? =
        if (valor.isBlank()) "Ingresa $campo" else null

    fun enteroNoNegativo(valor: String, campo: String): String? {
        if (valor.isBlank()) return "Ingresa $campo"
        val numero = valor.trim().toIntOrNull() ?: return "Ingresa un número entero"
        return if (numero < 0) "No puede ser menor a 0" else null
    }

    fun enteroPositivo(valor: String, campo: String): String? {
        if (valor.isBlank()) return "Ingresa $campo"
        val numero = valor.trim().toIntOrNull() ?: return "Ingresa un número entero"
        return if (numero <= 0) "Debe ser mayor a 0" else null
    }
}