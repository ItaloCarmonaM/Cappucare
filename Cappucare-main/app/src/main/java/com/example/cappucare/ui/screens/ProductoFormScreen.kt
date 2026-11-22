package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.ui.components.CampoSelector
import com.example.cappucare.ui.components.CampoTexto
import com.example.cappucare.ui.utils.anchoMaximoContenido
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.MainViewModel
import com.example.cappucare.viewmodel.ProductoFormViewModel

private val CATEGORIAS = listOf("Protección", "Higiene", "Material clínico", "Equipos", "Soluciones", "Otros")
private val UNIDADES = listOf("unidad", "caja", "paquete")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    mainViewModel: MainViewModel,
    vm: ProductoFormViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val anchoMaximo = rememberTamanoVentana().anchoMaximoContenido

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.esEdicion) "Editar producto" else "Nuevo producto") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize().imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = anchoMaximo)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CampoTexto(
                    valor = estado.codigo,
                    onValorChange = vm::onCodigoChange,
                    etiqueta = "Código",
                    icono = Icons.Default.Tag,
                    error = estado.errores.codigo
                )
                CampoTexto(
                    valor = estado.nombre,
                    onValorChange = vm::onNombreChange,
                    etiqueta = "Nombre",
                    icono = Icons.Default.Inventory2,
                    error = estado.errores.nombre
                )
                CampoTexto(
                    valor = estado.descripcion,
                    onValorChange = vm::onDescripcionChange,
                    etiqueta = "Descripción (opcional)",
                    icono = Icons.Default.Description
                )
                CampoSelector(
                    valor = estado.categoria,
                    opciones = CATEGORIAS,
                    onSeleccion = vm::onCategoriaChange,
                    etiqueta = "Categoría",
                    icono = Icons.Default.Category,
                    error = estado.errores.categoria
                )
                CampoTexto(
                    valor = estado.cantidad,
                    onValorChange = vm::onCantidadChange,
                    etiqueta = if (estado.esEdicion) "Cantidad (cambia con movimientos)" else "Cantidad inicial",
                    icono = Icons.Default.Numbers,
                    error = estado.errores.cantidad,
                    tipoTeclado = KeyboardType.Number,
                    // Al editar, el stock solo cambia registrando ingresos y salidas (RN6)
                    habilitado = !estado.esEdicion
                )
                CampoSelector(
                    valor = estado.unidadMedida,
                    opciones = UNIDADES,
                    onSeleccion = vm::onUnidadMedidaChange,
                    etiqueta = "Unidad de medida",
                    icono = Icons.Default.Straighten,
                    error = estado.errores.unidadMedida
                )
                CampoTexto(
                    valor = estado.nivelMinimo,
                    onValorChange = vm::onNivelMinimoChange,
                    etiqueta = "Nivel mínimo de existencias",
                    icono = Icons.Default.Warning,
                    error = estado.errores.nivelMinimo,
                    tipoTeclado = KeyboardType.Number
                )
                CampoTexto(
                    valor = estado.ubicacion,
                    onValorChange = vm::onUbicacionChange,
                    etiqueta = "Ubicación de almacenamiento",
                    icono = Icons.Default.Place,
                    error = estado.errores.ubicacion,
                    imeAction = ImeAction.Done
                )

                Button(
                    // Al guardar correctamente se vuelve a la pantalla anterior
                    onClick = { vm.guardar { mainViewModel.navigateBack() } },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    if (estado.guardando) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Text("  Guardar producto")
                    }
                }
            }
        }
    }
}
