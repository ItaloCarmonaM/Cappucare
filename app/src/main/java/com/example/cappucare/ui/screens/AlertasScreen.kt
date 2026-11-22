package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.navigation.Screen
import com.example.cappucare.ui.components.CargandoCentrado
import com.example.cappucare.ui.components.ErrorConReintento
import com.example.cappucare.ui.components.TarjetaProducto
import com.example.cappucare.ui.utils.columnasProductos
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.AlertasViewModel
import com.example.cappucare.viewmodel.MainViewModel

// Productos en stock bajo o agotados (RF7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    mainViewModel: MainViewModel,
    vm: AlertasViewModel = viewModel(factory = AlertasViewModel.Factory)
) {
    val estado by vm.estado.collectAsState()
    val productos = estado.productos
    val error = estado.error
    val columnas = rememberTamanoVentana().columnasProductos

    // Cada vez que la pantalla aparece se consulta de nuevo (el stock pudo cambiar)
    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        // El menú inferior ya lo maneja AppNavigation: aquí no se repiten los márgenes
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Alertas de stock") }) }
    ) { padding ->
        val modificador = Modifier.padding(padding)
        when {
            estado.cargando && productos.isEmpty() -> CargandoCentrado(modificador)

            error != null && productos.isEmpty() -> ErrorConReintento(error, vm::cargar, modificador)

            productos.isEmpty() -> {
                Box(
                    modificador.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos con stock bajo")
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnas),
                    modifier = modificador,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(productos, key = { it.id }) { producto ->
                        TarjetaProducto(
                            producto = producto,
                            onClick = { mainViewModel.navigateTo(Screen.ProductoDetalle(producto.id)) }
                        )
                    }
                }
            }
        }
    }
}
