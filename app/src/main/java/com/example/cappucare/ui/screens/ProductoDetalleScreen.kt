package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.model.EstadoProducto
import com.example.cappucare.navigation.Screen
import com.example.cappucare.ui.components.CargandoCentrado
import com.example.cappucare.ui.components.ErrorConReintento
import com.example.cappucare.ui.components.EstadoChip
import com.example.cappucare.ui.utils.anchoMaximoContenido
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.MainViewModel
import com.example.cappucare.viewmodel.ProductoDetalleViewModel
import com.example.cappucare.viewmodel.Sesion
import com.example.cappucare.ui.components.TarjetaWikipedia
import com.example.cappucare.viewmodel.WikipediaViewModel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    mainViewModel: MainViewModel,
    vm: ProductoDetalleViewModel = viewModel(factory = ProductoDetalleViewModel.Factory),
    wikipediaVm: WikipediaViewModel = viewModel(factory = WikipediaViewModel.Factory)
) {
    val estado by vm.estado.collectAsState()
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    val estadoWikipedia by wikipediaVm.estado.collectAsState()
    val producto = estado.producto
    val error = estado.error
    val anchoMaximo = rememberTamanoVentana().anchoMaximoContenido

    // Cada vez que la pantalla aparece se consulta de nuevo (el producto pudo editarse o cambiar su stock)
    LaunchedEffect(Unit) { vm.cargar() }

    // Consulta Wikipedia cuando ya se conoce el nombre del producto (independiente de los microservicios)
    val nombreProducto = producto?.nombre
    LaunchedEffect(nombreProducto) {
        nombreProducto?.let { wikipediaVm.cargar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // RN9: solo el Encargado edita y elimina productos
                    val p = producto
                    if (p != null && Sesion.esEncargado) {
                        IconButton(onClick = { mainViewModel.navigateTo(Screen.ProductoForm(p.id)) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar producto")
                        }
                        IconButton(
                            onClick = { mostrarConfirmacion = true },
                            enabled = !estado.eliminando
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar producto")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            val p = producto
            if (p == null) {
                if (error != null) {
                    ErrorConReintento(error, vm::cargar)
                } else {
                    CargandoCentrado()
                }
            } else {
                Column(
                    modifier = Modifier
                        .widthIn(max = anchoMaximo)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(p.nombre, style = MaterialTheme.typography.headlineSmall)
                    EstadoChip(p.estado)

                    if (p.estado != EstadoProducto.DISPONIBLE) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Este producto requiere reposición")
                            }
                        }
                    }

                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            FilaDato(Icons.Default.Tag, "Código", p.codigo)
                            FilaDato(Icons.Default.Description, "Descripción", p.descripcion.ifBlank { "Sin descripción" })
                            FilaDato(Icons.Default.Category, "Categoría", p.categoria)
                            FilaDato(Icons.Default.Inventory2, "Cantidad disponible", "${p.cantidad} ${p.unidadMedida}")
                            FilaDato(Icons.Default.Straighten, "Nivel mínimo", p.nivelMinimo.toString())
                            FilaDato(Icons.Default.Place, "Ubicación", p.ubicacion)
                        }
                    }

                    TarjetaWikipedia(
                        estado = estadoWikipedia,
                        onReintentar = wikipediaVm::reintentar
                    )
                    Button(
                        onClick = { mainViewModel.navigateTo(Screen.MovimientoForm(p.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = null)
                        Text(if (Sesion.esEncargado) "  Registrar movimiento" else "  Registrar venta")
                    }
                }
            }
        }
    }
    // Confirmación antes de eliminar
    if (mostrarConfirmacion && producto != null) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar \"${producto.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    vm.eliminar { mainViewModel.navigateBack() }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }

    // El servidor rechazó la eliminación (por ejemplo, el producto tiene movimientos)
    estado.errorEliminar?.let { mensaje ->
        AlertDialog(
            onDismissRequest = vm::descartarErrorEliminar,
            title = { Text("No se pudo eliminar") },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = vm::descartarErrorEliminar) { Text("Entendido") } }
        )
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium)
            Text(valor, style = MaterialTheme.typography.bodyLarge)
        }
    }
}