package com.itec.donelio.presentation.viewmodel.campania

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.use_case.ObtenerCampaniasActivasUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasInactivasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.EliminarCampaniaUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class GestionCampaniasViewModel @Inject constructor(
    private val obtenerCampaniasActivasUseCase: ObtenerCampaniasActivasUseCase,
    private val obtenerCampaniasInactivasUseCase: ObtenerCampaniasInactivasUseCase,
    private val eliminarCampaniaUseCase: EliminarCampaniaUseCase
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    val campaniasActivas: StateFlow<List<Campania>> = obtenerCampaniasActivasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val campaniasInactivas: StateFlow<List<Campania>> = obtenerCampaniasInactivasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun eliminarCampaniaPermanente(campania: Campania) {
        viewModelScope.launch {
            eliminarCampaniaUseCase(campania).collectLatest { result ->
                if (result is Resource.Error) {
                    _errorMessage.emit(result.message ?: "Ocurrió un error al eliminar la campaña")
                }
            }
        }
    }
}
