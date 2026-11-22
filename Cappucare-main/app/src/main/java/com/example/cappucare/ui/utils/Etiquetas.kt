package com.example.cappucare.ui.utils

import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.TipoMovimiento

// Textos que se muestran en pantalla para los enums de movimientos

fun MotivoSalida.etiqueta(): String = when (this) {
    MotivoSalida.VENTA -> "Venta"
    MotivoSalida.PRODUCTO_VENCIDO -> "Producto vencido"
    MotivoSalida.PRODUCTO_DANADO -> "Producto dañado"
    MotivoSalida.CORRECCION_STOCK -> "Corrección de stock"
}

fun TipoMovimiento.etiqueta(): String = when (this) {
    TipoMovimiento.INGRESO -> "Ingreso"
    TipoMovimiento.SALIDA -> "Salida"
}
