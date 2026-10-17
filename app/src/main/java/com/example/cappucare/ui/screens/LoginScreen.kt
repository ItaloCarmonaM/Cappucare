package com.example.cappucare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cappucare.navigation.Screen
import com.example.cappucare.ui.components.CampoTexto
import com.example.cappucare.ui.components.ErrorAnimado
import com.example.cappucare.ui.utils.anchoMaximoContenido
import com.example.cappucare.ui.utils.rememberTamanoVentana
import com.example.cappucare.viewmodel.LoginViewModel
import com.example.cappucare.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    vm: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val estado by vm.estado.collectAsState()
    val anchoMaximo = rememberTamanoVentana().anchoMaximoContenido

    // Al ingresar correctamente se pasa a Productos y se saca el login de la pila
    LaunchedEffect(estado.ingresoExitoso) {
        if (estado.ingresoExitoso) {
            mainViewModel.navigateTo(Screen.Productos, popUpToRoute = Screen.Login, inclusive = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding() // esta pantalla no tiene barra superior: respeta los márgenes del sistema
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = anchoMaximo).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("Cappucare", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Control de productos e insumos médicos",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            CampoTexto(
                valor = estado.correo,
                onValorChange = vm::onCorreoChange,
                etiqueta = "Correo",
                icono = Icons.Default.Email,
                error = estado.errorCorreo,
                tipoTeclado = KeyboardType.Email
            )
            CampoTexto(
                valor = estado.clave,
                onValorChange = vm::onClaveChange,
                etiqueta = "Contraseña",
                icono = Icons.Default.Lock,
                error = estado.errorClave,
                esClave = true,
                imeAction = ImeAction.Done
            )

            ErrorAnimado(estado.errorGeneral)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = vm::ingresar,
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Ingresar")
                }
            }
        }
    }
}