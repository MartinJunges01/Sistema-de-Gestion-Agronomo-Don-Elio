package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.CrearTareaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NuevaTareaFormState(
    val nombre: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val hora: String = "",
    val notificar: Boolean = true,
    val campaniaId: Int? = null,
    val isLoading: Boolean = false,
    val errorNombre: String? = null,
    val errorHora: String? = null,
    val errorCampania: String? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class NuevaTareaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val crearTareaUseCase: CrearTareaUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase
) : ViewModel() {

    private val initialCampaniaId = savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 }

    private val _state = MutableStateFlow(NuevaTareaFormState(campaniaId = initialCampaniaId))
    val state: StateFlow<NuevaTareaFormState> = _state.asStateFlow()

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    fun onCampaniaChange(id: Int) {
        _state.update { it.copy(campaniaId = id, errorCampania = null) }
    }

    fun guardar() {
        val current = _state.value

        if (current.nombre.isBlank()) {
            _state.update { it.copy(errorNombre = "El nombre es obligatorio") }
            return
        }

        if (current.hora.isBlank()) {
            _state.update { it.copy(errorHora = "La hora es obligatoria") }
            return
        }

        val regex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        if (!regex.matches(current.hora)) {
            _state.update { it.copy(errorHora = "Formato inválido (HH:mm)") }
            return
        }

        if (current.campaniaId == null) {
            _state.update { it.copy(errorCampania = "Debes seleccionar una campaña") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            crearTareaUseCase(
                nombre = current.nombre.trim(),
                fecha = current.fecha,
                hora = current.hora,
                notificar = current.notificar,
                idCampania = current.campaniaId
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
