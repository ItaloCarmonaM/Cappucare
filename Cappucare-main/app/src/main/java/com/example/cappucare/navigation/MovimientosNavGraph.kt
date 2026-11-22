
package com.example.cappucare.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cappucare.CappucareApp
import com.example.cappucare.ui.screens.AlertasScreen
import com.example.cappucare.ui.screens.HistorialScreen
import com.example.cappucare.ui.screens.MovimientoFormScreen
import com.example.cappucare.viewmodel.MainViewModel
import com.example.cappucare.viewmodel.MovimientoFormViewModel

fun NavGraphBuilder.movimientosNavGraph(viewModel: MainViewModel) {

    composable(Screen.Alertas.route) {
        AlertasScreen(viewModel)
    }

    composable(Screen.Historial.route) {
        HistorialScreen(viewModel)
    }

    composable(
        Screen.MovimientoForm().route,
        arguments = listOf(
            navArgument(Screen.ARG_PRODUCTO_ID) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val app = LocalContext.current.applicationContext as CappucareApp

        val movimientoViewModel: MovimientoFormViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = MovimientoFormViewModel.Factory(
                owner = backStackEntry,
                defaultArgs = backStackEntry.arguments,
                productoRepository = app.productoRepository,
                movimientoRepository = app.movimientoRepository
            )
        )

        MovimientoFormScreen(
            mainViewModel = viewModel,
            vm = movimientoViewModel
        )
    }
}

