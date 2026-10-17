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
        if (valor.isBlank()) "$campo es obligatorio" else null

    // RN2: la cantidad no puede ser negativa
    fun enteroNoNegativo(valor: String, campo: String): String? {
        if (valor.isBlank()) return "$campo es obligatorio"
        val numero = valor.trim().toIntOrNull() ?: return "$campo debe ser un número entero"
        return if (numero < 0) "$campo no puede ser negativo" else null
    }
}