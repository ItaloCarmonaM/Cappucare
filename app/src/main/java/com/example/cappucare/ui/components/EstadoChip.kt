package com.example.cappucare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.cappucare.model.EstadoProducto
import com.example.cappucare.ui.theme.ColorAgotado
import com.example.cappucare.ui.theme.ColorDisponible
import com.example.cappucare.ui.theme.ColorStockBajo

// Etiqueta con el estado de stock (RN7): Disponible, Stock bajo o Agotado
@Composable
fun EstadoChip(estado: EstadoProducto, modifier: Modifier = Modifier) {
    val (texto, color) = when (estado) {
        EstadoProducto.DISPONIBLE -> "Disponible" to ColorDisponible
        EstadoProducto.STOCK_BAJO -> "Stock bajo" to ColorStockBajo
        EstadoProducto.AGOTADO -> "Agotado" to ColorAgotado
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(6.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}