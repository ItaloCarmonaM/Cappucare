package com.example.cappucare.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Clases de tamaño de ventana (mismos cortes que Material: 600dp y 840dp)
enum class TamanoVentana { COMPACTO, MEDIO, EXPANDIDO }

@Composable
fun rememberTamanoVentana(): TamanoVentana {
    val anchoDp = LocalConfiguration.current.screenWidthDp
    return when {
        anchoDp < 600 -> TamanoVentana.COMPACTO
        anchoDp < 840 -> TamanoVentana.MEDIO
        else -> TamanoVentana.EXPANDIDO
    }
}

// Columnas de la grilla de productos según el tamaño
val TamanoVentana.columnasProductos: Int
    get() = when (this) {
        TamanoVentana.COMPACTO -> 1
        TamanoVentana.MEDIO -> 2
        TamanoVentana.EXPANDIDO -> 3
    }

// Ancho máximo del contenido en formularios y detalle (evita estirarlos en pantallas grandes)
val TamanoVentana.anchoMaximoContenido: Dp
    get() = when (this) {
        TamanoVentana.COMPACTO -> 480.dp
        TamanoVentana.MEDIO -> 560.dp
        TamanoVentana.EXPANDIDO -> 640.dp
    }