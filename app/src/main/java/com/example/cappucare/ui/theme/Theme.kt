package com.example.cappucare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Esquema de colores para el modo claro
private val EsquemaClaro = lightColorScheme(
    primary = VerdeAzulado,
    primaryContainer = VerdeAzuladoClaro,
    secondary = Naranjo,
    secondaryContainer = NaranjoClaro
)

// Esquema de colores para el modo oscuro
private val EsquemaOscuro = darkColorScheme(
    primary = VerdeAzuladoClaro,
    primaryContainer = VerdeAzuladoOscuro,
    secondary = NaranjoClaro,
    secondaryContainer = Naranjo
)

// Tema principal de la aplicación
@Composable
fun CappucareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colores = if (darkTheme) EsquemaOscuro else EsquemaClaro

    MaterialTheme(
        colorScheme = colores,
        typography = Typography,
        content = content
    )
}