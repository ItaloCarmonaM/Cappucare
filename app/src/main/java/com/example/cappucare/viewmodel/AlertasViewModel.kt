package com.example.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.Producto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// Productos en stock bajo o agotados
class AlertasViewModel(application: Application) : AndroidViewModel(application) {

    val productos: StateFlow<List<Producto>> = getApplication<CappucareApp>().productoRepository
        .obtenerStockBajo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}