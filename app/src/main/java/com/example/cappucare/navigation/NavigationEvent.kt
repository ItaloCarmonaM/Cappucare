package com.example.cappucare.navigation

// Representa los distintos tipos de eventos de navegación
sealed class NavigationEvent {

    // Navega a una ruta; opcionalmente limpia la pila hasta popUpToRoute
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Vuelve a la pantalla anterior de la pila
    object PopBackStack : NavigationEvent()

    // Navega "hacia arriba" en la jerarquía de la aplicación
    object NavigateUp : NavigationEvent()
}