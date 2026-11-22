package com.example.cappucare.model

// Estado de la pantalla de alertas (productos en stock bajo o agotados)
data class AlertasUiState(
    val productos: List<Producto> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)
