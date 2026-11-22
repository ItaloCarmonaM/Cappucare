package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucare.navigation.NavigationEvent
import com.example.cappucare.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Emite los eventos de navegación que la interfaz observa
class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    // Solo lectura para que la UI no pueda emitir eventos directamente
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Navega a la pantalla indicada
    fun navigateTo(
        screen: Screen,
        popUpToRoute: Screen? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(screen, popUpToRoute, inclusive, singleTop))
        }
    }

    // Vuelve atrás
    fun navigateBack() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.PopBackStack) }
    }

    // Navega hacia arriba (padre)
    fun navigateUp() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateUp) }
    }

    // Cierra la sesión y vuelve al login, limpiando la pila de navegación
    fun cerrarSesion() {
        Sesion.cerrar()
        navigateTo(Screen.Login, popUpToRoute = Screen.Productos, inclusive = true)
    }
}