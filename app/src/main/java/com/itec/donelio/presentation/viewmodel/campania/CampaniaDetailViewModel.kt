package com.itec.donelio.presentation.viewmodel.campania

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.FinalizarCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniaPorIdUseCase
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
    val finishSuccess: Boolean = false
)

@HiltViewModel
class CampaniaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val finalizarCampaniaUseCase: FinalizarCampaniaUseCase,
    private val obtenerCampaniaPorIdUseCase: ObtenerCampaniaPorIdUseCase
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1

    private val _state = MutableStateFlow(CampaniaDetailState())
    val state: StateFlow<CampaniaDetailState> = _state.asStateFlow()

    init {
        if (campaniaId > 0) {
            cargarCampania()
        } else {
            _state.update { it.copy(isLoading = false, error = "ID de campaña inválido") }
        }
    }

    private fun cargarCampania() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            obtenerCampaniaPorIdUseCase(campaniaId).collect { campania ->
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
