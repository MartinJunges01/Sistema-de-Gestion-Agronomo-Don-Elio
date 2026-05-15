package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.CrearTareaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NuevaTareaFormState(
    val nombre: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val hora: String = "",
    val notificar: Boolean = true,
    val isLoading: Boolean = false,
    val errorNombre: String? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class NuevaTareaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val crearTareaUseCase: CrearTareaUseCase
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1

    private val _state = MutableStateFlow(NuevaTareaFormState())
    val state: StateFlow<NuevaTareaFormState> = _state.asStateFlow()

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, errorNombre = null) }
    }

    fun onFechaChange(timestamp: Long) {
        _state.update { it.copy(fecha = timestamp) }
    }

    fun onHoraChange(value: String) {
        _state.update { it.copy(hora = value) }
    }

    fun onNotificarChange(value: Boolean) {
        _state.update { it.copy(notificar = value) }
    }

    fun guardar() {
        val current = _state.value

        if (current.nombre.isBlank()) {
            _state.update { it.copy(errorNombre = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            crearTareaUseCase(
                nombre = current.nombre.trim(),
                fecha = current.fecha,
                hora = current.hora,
                notificar = current.notificar,
                idCampania = campaniaId
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, errorNombre = resource.message) }
                }
            }
        }
    }

    fun resetGuardadoExitoso() {
        _state.update { it.copy(guardadoExitoso = false) }
    }
}
