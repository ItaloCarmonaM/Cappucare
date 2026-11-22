
package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.CappucareApp
import com.example.cappucare.navigation.Screen
import com.example.cappucare.ui.components.TarjetaProducto
import com.example.cappucare.ui.utils.columnasProductos
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.AlertasViewModel
import com.example.cappucare.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    mainViewModel: MainViewModel
) {
    val app = LocalContext.current.applicationContext as CappucareApp

    val vm: AlertasViewModel = viewModel(
        factory = AlertasViewModel.Factory(app.productoRepository)
    )

    val estado by vm.estado.collectAsState()
    val columnas = rememberTamanoVentana().columnasProductos

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("Alertas de stock") })
        }
    ) { padding ->
        when {
            estado.cargando -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            estado.error != null -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(estado.error ?: "Error al cargar alertas")
                        TextButton(onClick = { vm.cargar() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            estado.productos.isEmpty() -> {
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos con stock bajo")
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnas),
                    modifier = Modifier.padding(padding),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(estado.productos, key = { it.id }) { producto ->
                        TarjetaProducto(
                            producto = producto,
                            onClick = {
                                mainViewModel.navigateTo(
                                    Screen.ProductoDetalle(producto.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
