package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cappucare.CappucareApp
import com.example.cappucare.model.WikipediaUiState
import com.example.cappucare.repository.WikipediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Información de referencia del producto. Es independiente del detalle: si Wikipedia falla,
// solo se afecta la tarjeta y el resto de la pantalla sigue funcionando
class WikipediaViewModel(private val repositorio: WikipediaRepository) : ViewModel() {

    private val _estado = MutableStateFlow<WikipediaUiState>(WikipediaUiState.Cargando)
    val estado: StateFlow<WikipediaUiState> = _estado.asStateFlow()

    private var nombreActual: String? = null

    // No repite la consulta si ya está en curso o resuelta para el mismo producto (un error sí se reintenta)
    fun cargar(nombreProducto: String) {
        if (nombreProducto == nombreActual && _estado.value !is WikipediaUiState.Error) return
        consultar(nombreProducto)
    }

    fun reintentar() {
        nombreActual?.let { consultar(it) }
    }

    private fun consultar(nombreProducto: String) {
        nombreActual = nombreProducto
        _estado.value = WikipediaUiState.Cargando
        viewModelScope.launch {
            repositorio.buscarResumen(nombreProducto)
                .onSuccess { resumen ->
                    _estado.value = if (resumen != null) {
                        WikipediaUiState.Exito(resumen)
                    } else {
                        WikipediaUiState.SinInformacion
                    }
                }
                .onFailure { error ->
                    _estado.value = WikipediaUiState.Error(MensajesApi.de(error))
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CappucareApp
                WikipediaViewModel(app.wikipediaRepository)
            }
        }
    }
}