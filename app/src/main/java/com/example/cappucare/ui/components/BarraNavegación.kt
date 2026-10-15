package com.example.cappucare.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cappucare.model.Perfil
import com.example.cappucare.navigation.Screen

// Opción menú inferior
data class ItemMenu(
    val screen: Screen,
    val etiqueta: String,
    val icono: ImageVector,
    val soloEncargado: Boolean = false // El historial solo lo ve el Encargado
)

private val itemsMenu = listOf(
    ItemMenu(Screen.Productos, "Productos", Icons.Default.Inventory2),
    ItemMenu(Screen.Alertas, "Alertas", Icons.Default.Warning),
    ItemMenu(Screen.Historial, "Historial", Icons.Default.History, soloEncargado = true)
)

// Menú inferior de la aplicación
@Composable
fun BarraNavegacion(
    rutaActual: String?,
    perfil: Perfil,
    onItemClick: (Screen) -> Unit
) {
    NavigationBar {
        itemsMenu
            .filter { !it.soloEncargado || perfil == Perfil.ENCARGADO }
            .forEach { item ->
                NavigationBarItem(
                    selected = rutaActual == item.screen.route,
                    onClick = { onItemClick(item.screen) },
                    icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                    label = { Text(item.etiqueta) }
                )
            }
    }
}