package com.itec.donelio.presentation.viewmodel.observacion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.use_case.EditarObservacionUseCase
import com.itec.donelio.domain.use_case.EliminarObservacionUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerObservacionesPorCampaniaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.itec.donelio.domain.use_case.ValidarObservacionUseCase

@HiltViewModel
class ObservacionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerObservacionesPorCampaniaUseCase: ObtenerObservacionesPorCampaniaUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val editarObservacionUseCase: EditarObservacionUseCase,
    private val eliminarObservacionUseCase: EliminarObservacionUseCase,
    private val validarObservacionUseCase: ValidarObservacionUseCase
) : ViewModel() {

    fun validarEdicion(texto: String, imagenUri: String?): Boolean {
        return validarObservacionUseCase(texto, imagenUri)
    }

    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 })
    val campaniaIdSeleccionada = _campaniaIdSeleccionada.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val isCampaniaValid: StateFlow<Boolean> = _campaniaIdSeleccionada
        .map { it != null && it != -1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val observaciones: StateFlow<List<Observacion>> = _campaniaIdSeleccionada.flatMapLatest { id ->
        if (id != null && id != -1) obtenerObservacionesPorCampaniaUseCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Selecciona una campaña y actualiza el estado correspondiente.
     * @param id El ID de la campaña seleccionada.
     */
    fun seleccionarCampania(id: Int) { _campaniaIdSeleccionada.value = id }

    /**
     * Limpia el mensaje de error actual.
     */
    fun clearError() { _errorMessage.value = null }

    /**
     * Edita una observación existente y actualiza el estado en caso de error.
     * @param observacion La observación con los datos editados.
     */
    fun editarObservacion(observacion: Observacion) {
        viewModelScope.launch {
            editarObservacionUseCase(observacion).collect { resource ->
                when (resource) {
                    is com.itec.donelio.domain.model.Resource.Error -> {
                        _errorMessage.value = resource.message
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * Elimina una observación y actualiza el estado en caso de error.
     * @param observacion La observación a eliminar.
     */
    fun eliminarObservacion(observacion: Observacion) {
        viewModelScope.launch {
            eliminarObservacionUseCase(observacion).collect { resource ->
                when (resource) {
                    is com.itec.donelio.domain.model.Resource.Error -> {
                        _errorMessage.value = resource.message
                    }
                    else -> {}
                }
            }
        }
    }
}
