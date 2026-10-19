package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.ui.theme.ColorAgotado
import com.example.cappucare.ui.theme.ColorDisponible
import com.example.cappucare.ui.utils.columnasProductos
import com.example.cappucare.ui.utils.etiqueta
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.HistorialViewModel
import com.example.cappucare.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Historial de movimientos (RF6): fecha, tipo, cantidad, motivo y responsable (RN6)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    mainViewModel: MainViewModel,
    vm: HistorialViewModel = viewModel()
) {
    val movimientos by vm.movimientos.collectAsState()
    val columnas = rememberTamanoVentana().columnasProductos

    Scaffold(
        // El menú inferior ya lo maneja AppNavigation: aquí no se repiten los márgenes
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Historial de movimientos") }) }
    ) { padding ->
        if (movimientos.isEmpty()) {
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no hay movimientos registrados")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnas),
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(movimientos, key = { it.movimiento.id }) { detalle ->
                    TarjetaMovimiento(detalle)
                }
            }
        }
    }
}

@Composable
private fun TarjetaMovimiento(detalle: MovimientoDetalle) {
    val m = detalle.movimiento
    val esIngreso = m.tipo == TipoMovimiento.INGRESO
    val color = if (esIngreso) ColorDisponible else ColorAgotado
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(m.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (esIngreso) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = m.tipo.etiqueta(),
                tint = color
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(detalle.productoNombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${m.tipo.etiqueta()} de ${m.cantidad}" +
                            (m.motivo?.let { " · ${it.etiqueta()}" } ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
                Text("Responsable: ${detalle.usuarioNombre}", style = MaterialTheme.typography.bodySmall)
                Text(fecha, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
