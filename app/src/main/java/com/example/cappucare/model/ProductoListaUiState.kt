package com.example.cappucare.model

data class ProductoListaUiState(
    val productos: List<Producto> = emptyList(),
    val busqueda: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)