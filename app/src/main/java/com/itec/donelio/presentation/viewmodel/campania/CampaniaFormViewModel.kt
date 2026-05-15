package com.itec.donelio.presentation.viewmodel.campania

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.use_case.CrearCampaniaUseCase
import com.itec.donelio.domain.use_case.EditarCampaniaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaniaFormState(
    val nombre: String = "",
    val cultivo: String = "",
    val fechaInicio: Long = System.currentTimeMillis(),
    val errorNombre: String? = null,
    val errorCultivo: String? = null,
    val errorFecha: String? = null,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val campaniaId: Int? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class CampaniaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val crearCampaniaUseCase: CrearCampaniaUseCase,
    private val editarCampaniaUseCase: EditarCampaniaUseCase,
    private val campaniaRepository: CampaniaRepository
) : ViewModel() {

    private val campaniaId: Int? = savedStateHandle.get<Int>("campaniaId")

    private val _state = MutableStateFlow(CampaniaFormState())
    val state: StateFlow<CampaniaFormState> = _state.asStateFlow()

    init {
        if (campaniaId != null && campaniaId > 0) {
            cargarCampania(campaniaId)
        }
    }

    private fun cargarCampania(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val campania = campaniaRepository.getCampaniaById(id)
            if (campania != null) {
                _state.update {
                    it.copy(
                        nombre = campania.nombre,
                        cultivo = campania.cultivo,
                        fechaInicio = campania.fechaInicio,
                        isEditMode = true,
                        campaniaId = campania.id,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, errorNombre = null) }
    }

    fun onCultivoChange(value: String) {
        _state.update { it.copy(cultivo = value, errorCultivo = null) }
    }

    fun onFechaChange(timestamp: Long) {
        _state.update { it.copy(fechaInicio = timestamp, errorFecha = null) }
    }

    fun guardar() {
        val current = _state.value
        var hasError = false

        if (current.nombre.isBlank()) {
            _state.update { it.copy(errorNombre = "El nombre es obligatorio") }
            hasError = true
        }
        if (current.cultivo.isBlank()) {
            _state.update { it.copy(errorCultivo = "El cultivo es obligatorio") }
            hasError = true
        }
        if (current.fechaInicio <= 0) {
            _state.update { it.copy(errorFecha = "Seleccione una fecha") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            if (current.isEditMode && current.campaniaId != null) {
                val campania = Campania(
                    id = current.campaniaId,
                    nombre = current.nombre.trim(),
                    fechaInicio = current.fechaInicio,
                    estaActiva = true,
                    cultivo = current.cultivo.trim()
                )
                editarCampaniaUseCase(campania).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                        is Resource.Success -> _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
                        is Resource.Error -> _state.update { it.copy(isLoading = false, errorNombre = resource.message) }
                    }
                }
            } else {
                crearCampaniaUseCase(
                    nombre = current.nombre.trim(),
                    cultivo = current.cultivo.trim(),
                    fechaInicio = current.fechaInicio
                ).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                        is Resource.Success -> _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
                        is Resource.Error -> _state.update { it.copy(isLoading = false, errorNombre = resource.message) }
                    }
                }
            }
        }
    }

    fun resetGuardadoExitoso() {
        _state.update { it.copy(guardadoExitoso = false) }
    }
}
