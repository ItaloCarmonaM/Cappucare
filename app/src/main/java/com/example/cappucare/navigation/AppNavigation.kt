package com.example.cappucare.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cappucare.model.Perfil
import com.example.cappucare.ui.components.BarraNavegacion
import com.example.cappucare.viewmodel.MainViewModel
import androidx.compose.runtime.collectAsState
import com.example.cappucare.viewmodel.Sesion

// Pantallas donde se muestra el menú inferior
private val rutasConBarra = listOf(
    Screen.Productos.route,
    Screen.Alertas.route,
    Screen.Historial.route
)

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val usuario by Sesion.usuarioFlow.collectAsState()

    // Escucha los eventos de navegación emitidos por el ViewModel
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.crearRuta()) {
                        event.popUpToRoute?.let { destino ->
                            popUpTo(destino.route) { inclusive = event.inclusive }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    Scaffold(
        // Cada pantalla maneja sus propios márgenes del sistema (barra superior)
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (rutaActual in rutasConBarra) {
                BarraNavegacion(
                    rutaActual = rutaActual,
                    perfil = usuario?.perfil ?: Perfil.CONSULTOR, // Sin sesión se usa el perfil de menos permisos
                    onItemClick = { destino ->
                        viewModel.navigateTo(destino, popUpToRoute = Screen.Productos, singleTop = true)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Cada parte registra sus pantallas en su propio archivo
            productosNavGraph(viewModel)
            movimientosNavGraph(viewModel)
        }
    }
}