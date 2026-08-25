package com.itec.donelio.presentation.viewmodel.campania

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.CrearCampaniaUseCase
import com.itec.donelio.domain.use_case.EditarCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniaPorIdUseCase
import com.itec.donelio.domain.use_case.ValidarDatosCampaniaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaniaFormState(
    val nombre: String = "",
    val hectareas: String = "",
    val cultivo: String = "",
    val fechaInicio: Long = System.currentTimeMillis(),
    val errorNombre: String? = null,
    val errorHectareas: String? = null,
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
    private val obtenerCampaniaPorIdUseCase: ObtenerCampaniaPorIdUseCase,
    private val validarDatosCampaniaUseCase: ValidarDatosCampaniaUseCase
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
            val campania = obtenerCampaniaPorIdUseCase(id).first()
            if (campania != null) {
                _state.update {
                    it.copy(
                        nombre = campania.nombre,
                        hectareas = campania.hectareas.toString(),
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
    
    fun onHectareasChange(value: String) {
        _state.update { it.copy(hectareas = value, errorHectareas = null) }
    }

    fun onCultivoChange(value: String) {
        _state.update { it.copy(cultivo = value, errorCultivo = null) }
    }

    fun onFechaChange(timestamp: Long) {
        _state.update { it.copy(fechaInicio = timestamp, errorFecha = null) }
    }

    fun guardar() {
        val current = _state.value
        
        val hectareasParsed = current.hectareas.toDoubleOrNull()
        
        val resultadoValidacion = validarDatosCampaniaUseCase(
            nombre = current.nombre,
            hectareas = hectareasParsed,
            cultivo = current.cultivo,
            fechaInicio = current.fechaInicio,
            isEditMode = current.isEditMode
        )

        if (!resultadoValidacion.esValido) {
            _state.update { it.copy(
                errorNombre = resultadoValidacion.errorNombre,
                errorHectareas = resultadoValidacion.errorHectareas,
                errorCultivo = resultadoValidacion.errorCultivo,
                errorFecha = resultadoValidacion.errorFecha
            ) }
            return
        }

        viewModelScope.launch {
            if (current.isEditMode && current.campaniaId != null) {
                val campania = Campania(
                    id = current.campaniaId,
                    nombre = current.nombre.trim(),
                    hectareas = hectareasParsed!!,
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
                    hectareas = hectareasParsed!!,
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
