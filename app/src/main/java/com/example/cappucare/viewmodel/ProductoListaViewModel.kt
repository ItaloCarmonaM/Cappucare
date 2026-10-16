package com.example.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.Producto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

// Lista de productos con búsqueda por nombre o código
@OptIn(ExperimentalCoroutinesApi::class)
class ProductoListaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = getApplication<CappucareApp>().productoRepository

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    // Cuando cambia texto de búsqueda, se consulta la base de datos
    val productos: StateFlow<List<Producto>> = _busqueda
        .flatMapLatest { texto ->
            if (texto.isBlank()) repository.obtenerTodos() else repository.buscar(texto)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBusquedaChange(valor: String) {
        _busqueda.value = valor
    }
}