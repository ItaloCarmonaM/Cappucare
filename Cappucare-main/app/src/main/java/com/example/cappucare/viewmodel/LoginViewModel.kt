package com.example.cappucare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cappucare.CappucareApp
import com.example.cappucare.data.remote.ApiException
import com.example.cappucare.model.LoginUiState
import com.example.cappucare.repository.UsuarioRepository
import com.example.cappucare.viewmodel.validacion.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(private val repositorio: UsuarioRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Si el campo ya tenía error, se revalida mientras el usuario corrige
    fun onCorreoChange(valor: String) = _estado.update {
        it.copy(
            correo = valor,
            errorCorreo = if (it.errorCorreo != null) Validadores.correo(valor) else null,
            errorGeneral = null
        )
    }

    fun onClaveChange(valor: String) = _estado.update {
        it.copy(
            clave = valor,
            errorClave = if (it.errorClave != null) Validadores.clave(valor) else null,
            errorGeneral = null
        )
    }

    fun ingresar() {
        val actual = _estado.value
        if (actual.cargando) return

        val errorCorreo = Validadores.correo(actual.correo)
        val errorClave = Validadores.clave(actual.clave)
        if (errorCorreo != null || errorClave != null) {
            _estado.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errorGeneral = null) }

            repositorio.autenticar(actual.correo, actual.clave)
                .onSuccess { usuario ->
                    Sesion.iniciar(usuario)
                    _estado.update { it.copy(cargando = false, ingresoExitoso = true) }
                }
                .onFailure { error ->
                    // 401 = credenciales incorrectas; cualquier otro error muestra su propio mensaje
                    val mensaje = if (error is ApiException && error.codigo == 401) {
                        "Correo o contraseña incorrectos"
                    } else {
                        MensajesApi.de(error)
                    }
                    _estado.update { it.copy(cargando = false, errorGeneral = mensaje) }
                }
        }
    }

    companion object {
        // Crea el ViewModel con el repositorio que guarda CappucareApp
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CappucareApp
                LoginViewModel(app.usuarioRepository)
            }
        }
    }
}
