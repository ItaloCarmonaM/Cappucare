package com.example.cappucare.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cappucare.ui.screens.LoginScreen
import com.example.cappucare.ui.screens.ProductoDetalleScreen
import com.example.cappucare.ui.screens.ProductoFormScreen
import com.example.cappucare.ui.screens.ProductosScreen
import com.example.cappucare.viewmodel.MainViewModel

// Rutas de productos y acceso: Login, Productos, ProductoDetalle y ProductoForm
fun NavGraphBuilder.productosNavGraph(viewModel: MainViewModel) {

    val argumentoId = listOf(
        navArgument(Screen.ARG_PRODUCTO_ID) { type = NavType.IntType }
    )

    composable(Screen.Login.route) {
        LoginScreen(viewModel)
    }

    composable(Screen.Productos.route) {
        ProductosScreen(viewModel)
    }

    composable(Screen.ProductoDetalle(0).route, arguments = argumentoId) {
        ProductoDetalleScreen(viewModel)
    }

    composable(Screen.ProductoForm().route, arguments = argumentoId) {
        ProductoFormScreen(viewModel)
    }
}