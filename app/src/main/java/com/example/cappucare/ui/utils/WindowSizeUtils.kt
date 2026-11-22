package com.example.cappucare.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Tamaños de ventana que usa la app
enum class TamanoVentana { COMPACTO, MEDIO, EXPANDIDO }

// Busca la Activity que contiene a este contexto
private tailrec fun Context.buscarActividad(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.buscarActividad()
    else -> null
}

// Calcula el tamaño actual con la API oficial Window Size Classes
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberTamanoVentana(): TamanoVentana {
    // Sin Activity (por ejemplo en un Preview) se asume el tamaño compacto
    val activity = LocalContext.current.buscarActividad() ?: return TamanoVentana.COMPACTO
    return when (calculateWindowSizeClass(activity).widthSizeClass) {
        WindowWidthSizeClass.Compact -> TamanoVentana.COMPACTO
        WindowWidthSizeClass.Medium -> TamanoVentana.MEDIO
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

// Ancho máximo del contenido en formularios y detalle
val TamanoVentana.anchoMaximoContenido: Dp
    get() = when (this) {
        TamanoVentana.COMPACTO -> 480.dp
        TamanoVentana.MEDIO -> 560.dp
        TamanoVentana.EXPANDIDO -> 640.dp
    }