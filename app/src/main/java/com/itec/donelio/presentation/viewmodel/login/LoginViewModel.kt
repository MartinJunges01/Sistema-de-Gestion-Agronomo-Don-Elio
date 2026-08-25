package com.itec.donelio.presentation.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.core.SessionManager
import com.itec.donelio.domain.use_case.LoginUseCase
import com.itec.donelio.domain.use_case.RegistroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false,
    val registroExitoso: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registroUseCase: RegistroUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun login(nombreUsuario: String, contrasena: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val usuario = loginUseCase(nombreUsuario, contrasena)
                if (usuario != null) {
                    // Persistir el nombre del usuario en la sesion para mostrarlo en el Dashboard
                    sessionManager.saveUserName(usuario.nombre)
                    _state.update { it.copy(isLoading = false, loginExitoso = true) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Credenciales inválidas") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Error de inicio de sesión") }
            }
        }
    }

    fun registro(nombre: String, nombreUsuario: String, contrasena: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                registroUseCase(nombre, nombreUsuario, contrasena)
                _state.update { it.copy(isLoading = false, registroExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Error de registro") }
            }
        }
    }

    fun loginInvitado() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                sessionManager.saveUserName("Invitado")
                _state.update { it.copy(isLoading = false, loginExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al iniciar sesión como invitado") }
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(error = null) }
    }
}
