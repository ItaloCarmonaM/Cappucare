package com.example.cappucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.MovimientoDetalle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// Historial de movimientos, del más reciente al más antiguo
class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    val movimientos: StateFlow<List<MovimientoDetalle>> = getApplication<CappucareApp>().movimientoRepository
        .obtenerHistorial()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}