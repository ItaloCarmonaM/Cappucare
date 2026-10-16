package com.example.cappucare.model

// Estado del formulario de producto
data class ProductoFormUiState(
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val cantidad: String = "",
    val unidadMedida: String = "",
    val nivelMinimo: String = "",
    val ubicacion: String = "",
    val esEdicion: Boolean = false,
    val guardando: Boolean = false,
    val errores: ProductoFormErrores = ProductoFormErrores()
)

// Errores por campo del formulario de producto (null = sin error)
data class ProductoFormErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val cantidad: String? = null,
    val unidadMedida: String? = null,
    val nivelMinimo: String? = null,
    val ubicacion: String? = null
) {
    fun hayErrores(): Boolean = listOf(
        codigo, nombre, categoria, cantidad, unidadMedida, nivelMinimo, ubicacion
    ).any { it != null }
}