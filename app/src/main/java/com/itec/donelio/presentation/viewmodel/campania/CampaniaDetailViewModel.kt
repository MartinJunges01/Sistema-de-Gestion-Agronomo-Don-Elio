package com.itec.donelio.presentation.viewmodel.campania

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.FinalizarCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniaPorIdUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaniaDetailState(
    val campania: Campania? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val finishSuccess: Boolean = false,
    val idAnterior: Int? = null,
    val idSiguiente: Int? = null
)

@HiltViewModel
class CampaniaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val finalizarCampaniaUseCase: FinalizarCampaniaUseCase,
    private val obtenerCampaniaPorIdUseCase: ObtenerCampaniaPorIdUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase
) : ViewModel() {

    private var currentCampaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1
    private var allCampanias: List<Campania> = emptyList()

    private val _state = MutableStateFlow(CampaniaDetailState())
    val state: StateFlow<CampaniaDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            obtenerCampaniasUseCase().collect { campanias ->
                allCampanias = campanias
                actualizarNavegacion()
            }
        }
        if (currentCampaniaId > 0) {
            cargarCampania()
        } else {
            _state.update { it.copy(isLoading = false, error = "ID de campaña inválido") }
        }
    }

    fun navegarA(nuevoId: Int) {
        currentCampaniaId = nuevoId
        cargarCampania()
        actualizarNavegacion()
    }

    private fun actualizarNavegacion() {
        if (allCampanias.isEmpty() || currentCampaniaId <= 0) return
        
        val index = allCampanias.indexOfFirst { it.id == currentCampaniaId }
        if (index != -1) {
            val prev = if (index > 0) allCampanias[index - 1].id else null
            val next = if (index < allCampanias.size - 1) allCampanias[index + 1].id else null
            _state.update { it.copy(idAnterior = prev, idSiguiente = next) }
        } else {
            _state.update { it.copy(idAnterior = null, idSiguiente = null) }
        }
    }

    private fun cargarCampania() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            obtenerCampaniaPorIdUseCase(currentCampaniaId).collect { campania ->
                if (campania != null) {
                    _state.update { it.copy(campania = campania, isLoading = false, error = null) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Campaña no encontrada") }
                }
            }
        }
    }

    fun finalizarCampania() {
        val campania = _state.value.campania ?: return
        viewModelScope.launch {
            finalizarCampaniaUseCase(campania).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, finishSuccess = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = resource.message) }
                }
            }
        }
    }
}
