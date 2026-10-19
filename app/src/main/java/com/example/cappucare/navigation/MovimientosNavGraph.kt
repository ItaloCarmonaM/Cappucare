package com.example.cappucare.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cappucare.ui.screens.AlertasScreen
import com.example.cappucare.ui.screens.HistorialScreen
import com.example.cappucare.ui.screens.MovimientoFormScreen
import com.example.cappucare.viewmodel.MainViewModel

// Rutas de movimientos y alertas: MovimientoForm, Historial y Alertas
fun NavGraphBuilder.movimientosNavGraph(viewModel: MainViewModel) {

    composable(Screen.Alertas.route) {
        AlertasScreen(viewModel)
    }

    composable(Screen.Historial.route) {
        HistorialScreen(viewModel)
    }

    composable(
        Screen.MovimientoForm().route,
        arguments = listOf(navArgument(Screen.ARG_PRODUCTO_ID) { type = NavType.IntType })
    ) {
        MovimientoFormScreen(viewModel)
    }
}
