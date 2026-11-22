package com.example.cappucare.model

// Estado de la pantalla de historial de movimientos
data class HistorialUiState(
    val movimientos: List<MovimientoDetalle> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)
