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
import javax.inject.Inject

@HiltViewModel
class ObservacionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerObservacionesPorCampaniaUseCase: ObtenerObservacionesPorCampaniaUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val editarObservacionUseCase: EditarObservacionUseCase,
    private val eliminarObservacionUseCase: EliminarObservacionUseCase
) : ViewModel() {

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

    fun seleccionarCampania(id: Int) { _campaniaIdSeleccionada.value = id }
    fun clearError() { _errorMessage.value = null }

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
