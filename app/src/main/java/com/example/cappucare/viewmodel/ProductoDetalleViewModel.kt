package com.example.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.Producto
import com.example.cappucare.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// Detalle de un producto
class ProductoDetalleViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val productoId: Int = savedStateHandle.get<Int>(Screen.ARG_PRODUCTO_ID) ?: 0

    // Se actualiza cuando cambia el producto en la base de datos
    val producto: StateFlow<Producto?> = getApplication<CappucareApp>().productoRepository
        .observarPorId(productoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}