package com.example.cappucare.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.Producto
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.notification.Notificaciones
import com.example.cappucare.notification.Vibracion
import com.example.cappucare.ui.components.CampoSelector
import com.example.cappucare.ui.components.CampoTexto
import com.example.cappucare.ui.components.ErrorAnimado
import com.example.cappucare.ui.utils.anchoMaximoContenido
import com.example.cappucare.ui.utils.etiqueta
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.MainViewModel
import com.example.cappucare.viewmodel.MovimientoFormViewModel
import com.example.cappucare.viewmodel.Sesion
import kotlinx.coroutines.delay

// Texto con el que se muestra cada producto en el selector
private fun Producto.textoSelector() = "$nombre ($codigo)"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
    mainViewModel: MainViewModel,
    vm: MovimientoFormViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val contexto = LocalContext.current
    val anchoMaximo = rememberTamanoVentana().anchoMaximoContenido
    var confirmado by remember { mutableStateOf(false) }

    // El Consultor solo puede registrar ventas (RN9)
    LaunchedEffect(Unit) {
        Sesion.usuario?.let { vm.configurarPerfil(it.perfil) }
    }

    // Tras guardar se muestra la confirmación un momento y se vuelve atrás
    LaunchedEffect(confirmado) {
        if (confirmado) {
            delay(1200)
            mainViewModel.navigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (Sesion.esEncargado) "Registrar movimiento" else "Registrar venta") },
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
                CampoSelector(
                    valor = estado.producto?.textoSelector() ?: "",
                    opciones = estado.productos.map { it.textoSelector() },
                    onSeleccion = { texto ->
                        estado.productos.firstOrNull { it.textoSelector() == texto }
                            ?.let(vm::onProductoChange)
                    },
                    etiqueta = "Producto",
                    icono = Icons.Default.Inventory2,
                    error = estado.errores.producto,
                    habilitado = !estado.guardando && !confirmado
                )

                estado.producto?.let {
                    Text(
                        text = "Stock actual: ${it.cantidad} ${it.unidadMedida}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Tipo de movimiento: Ingreso o Salida
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoMovimiento.entries.forEach { tipo ->
                        FilterChip(
                            selected = estado.tipo == tipo,
                            onClick = { vm.onTipoChange(tipo) },
                            label = { Text(tipo.etiqueta()) },
                            enabled = !estado.soloVenta && !estado.guardando && !confirmado
                        )
                    }
                }

                CampoTexto(
                    valor = estado.cantidad,
                    onValorChange = vm::onCantidadChange,
                    etiqueta = "Cantidad",
                    icono = Icons.Default.Numbers,
                    error = estado.errores.cantidad,
                    tipoTeclado = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    habilitado = !estado.guardando && !confirmado
                )

                // El motivo solo aplica a las salidas (RN5)
                AnimatedVisibility(
                    visible = estado.tipo == TipoMovimiento.SALIDA,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CampoSelector(
                        valor = estado.motivo?.etiqueta() ?: "",
                        opciones = MotivoSalida.entries.map { it.etiqueta() },
                        onSeleccion = { texto ->
                            MotivoSalida.entries.firstOrNull { it.etiqueta() == texto }
                                ?.let(vm::onMotivoChange)
                        },
                        etiqueta = "Motivo de la salida",
                        icono = Icons.Default.QuestionAnswer,
                        error = estado.errores.motivo,
                        habilitado = !estado.soloVenta && !estado.guardando && !confirmado
                    )
                }

                ErrorAnimado(estado.errorGeneral)

                Button(
                    onClick = {
                        val usuario = Sesion.usuario
                        if (usuario != null) {
                            vm.registrar(usuario.id) { resultado ->
                                // RN8: solo avisa cuando recién llega al nivel mínimo
                                if (resultado.alcanzoNivelMinimo) {
                                    Notificaciones.notificarStockBajo(contexto, resultado.producto)
                                    Vibracion.vibrar(contexto)
                                }
                                confirmado = true
                            }
                        }
                    },
                    enabled = !estado.guardando && !confirmado,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    if (estado.guardando) {
                        // Loader mientras se guarda
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Text("  Guardar movimiento")
                    }
                }

                // Confirmación animada al guardar
                AnimatedVisibility(
                    visible = confirmado,
                    enter = fadeIn() + scaleIn() + expandVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.padding(4.dp))
                        Text("Movimiento registrado", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
