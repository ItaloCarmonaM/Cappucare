package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.model.MovimientoDetalle
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.ui.theme.ColorAgotado
import com.example.cappucare.ui.theme.ColorDisponible
import com.example.cappucare.ui.utils.anchoMaximoContenido
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
    val anchoMaximo = rememberTamanoVentana().anchoMaximoContenido

    Scaffold(
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
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(modifier = Modifier.widthIn(max = anchoMaximo).fillMaxWidth()) {
                    items(movimientos, key = { it.movimiento.id }) { detalle ->
                        FilaMovimiento(detalle)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaMovimiento(detalle: MovimientoDetalle) {
    val m = detalle.movimiento
    val esIngreso = m.tipo == TipoMovimiento.INGRESO
    val color = if (esIngreso) ColorDisponible else ColorAgotado
    val signo = if (esIngreso) "+" else "-"
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(m.fecha))

    ListItem(
        leadingContent = {
            Icon(
                imageVector = if (esIngreso) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = m.tipo.etiqueta(),
                tint = color
            )
        },
        headlineContent = {
            Text(detalle.productoNombre, style = MaterialTheme.typography.titleMedium)
        },
        supportingContent = {
            Column {
                Text(m.tipo.etiqueta() + (m.motivo?.let { " · ${it.etiqueta()}" } ?: ""))
                Text("${detalle.usuarioNombre} · $fecha", style = MaterialTheme.typography.bodySmall)
            }
        },
        trailingContent = {
            Text("$signo${m.cantidad}", style = MaterialTheme.typography.titleMedium, color = color)
        }
    )
}