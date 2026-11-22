package com.example.cappucare.model

sealed interface WikipediaUiState {
    data object Cargando : WikipediaUiState
    data class Exito(val resumen: WikipediaResumen) : WikipediaUiState
    data object SinInformacion : WikipediaUiState
    data class Error(val mensaje: String) : WikipediaUiState
}