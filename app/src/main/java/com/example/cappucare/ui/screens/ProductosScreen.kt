package com.example.cappucare.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.navigation.Screen
import com.example.cappucare.ui.components.TarjetaProducto
import com.example.cappucare.ui.utils.columnasProductos
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.MainViewModel
import com.example.cappucare.viewmodel.ProductoListaViewModel
import com.example.cappucare.viewmodel.Sesion
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    mainViewModel: MainViewModel,
    vm: ProductoListaViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val columnas = rememberTamanoVentana().columnasProductos

    Scaffold(
        // El menú inferior ya lo maneja AppNavigation: aquí no se repiten los márgenes
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Productos") }) },
        floatingActionButton = {
            // RN9: solo el Encargado crea productos
            if (Sesion.esEncargado) {
                FloatingActionButton(
                    onClick = { mainViewModel.navigateTo(Screen.ProductoForm()) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::onBusquedaChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar por nombre o código") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { vm.onBusquedaChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (busqueda.isBlank()) "No hay productos registrados" else "Sin resultados para \"$busqueda\"")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnas),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp) // espacio para el botón flotante
                ) {
                    itemsIndexed(productos, key = { _, p -> p.id }) { indice, producto ->
                        EntradaAnimada(indice) {
                            TarjetaProducto(
                                producto = producto,
                                onClick = {
                                    mainViewModel.navigateTo(Screen.ProductoDetalle(producto.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Animación de entrada de cada tarjeta: aparece con fundido y desplazamiento, una tras otra (IE 2.2.2)
@Composable
private fun EntradaAnimada(indice: Int, contenido: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(minOf(indice, 8) * 60L) // escalonado, con tope para listas largas
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 }
    ) {
        contenido()
    }
}
