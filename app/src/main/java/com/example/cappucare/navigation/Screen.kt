package com.example.cappucare.navigation

// Define las rutas de la aplicación
sealed class Screen(val route: String) {

    open fun crearRuta(): String = route

    // Rutas simples
    data object Login : Screen("login_page")
    data object Productos : Screen("productos_page")
    data object Alertas : Screen("alertas_page")
    data object Historial : Screen("historial_page")

    // Rutas con argumentos

    // Detalle de un producto
    data class ProductoDetalle(val productoId: Int) : Screen("producto_detalle/{$ARG_PRODUCTO_ID}") {
        override fun crearRuta() = route.replace("{$ARG_PRODUCTO_ID}", productoId.toString())
    }

    // Formulario de producto (productoId = 0 significa producto nuevo)
    data class ProductoForm(val productoId: Int = 0) : Screen("producto_form/{$ARG_PRODUCTO_ID}") {
        override fun crearRuta() = route.replace("{$ARG_PRODUCTO_ID}", productoId.toString())
    }

    // Formulario de movimiento (productoId = 0 significa sin producto preseleccionado)
    data class MovimientoForm(val productoId: Int = 0) : Screen("movimiento_form/{$ARG_PRODUCTO_ID}") {
        override fun crearRuta() = route.replace("{$ARG_PRODUCTO_ID}", productoId.toString())
    }

    companion object {
        const val ARG_PRODUCTO_ID = "productoId"
    }
}