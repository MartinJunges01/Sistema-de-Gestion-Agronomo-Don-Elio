package com.itec.donelio.presentation.viewmodel.campania

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.use_case.EliminarCampaniaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaniaDetailState(
    val campaign: Campania? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val deleteSuccess: Boolean = false
)

@HiltViewModel
class CampaniaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val campaniaRepository: CampaniaRepository,
    private val eliminarCampaniaUseCase: EliminarCampaniaUseCase
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
            val campania = campaniaRepository.getCampaniaById(campaniaId)
            if (campania != null) {
                _state.update { it.copy(campaign = campania, isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Campaña no encontrada") }
            }
        }
    }

    fun eliminarCampania() {
        val campania = _state.value.campaign ?: return
        viewModelScope.launch {
            eliminarCampaniaUseCase(campania).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, deleteSuccess = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = resource.message) }
                }
            }
        }
    }
}
