package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import com.itec.donelio.domain.use_case.ConfirmarTareaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tareaRepository: TareaRepository,
    private val confirmarTareaUseCase: ConfirmarTareaUseCase
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1

    val tareas: StateFlow<List<Tarea>> = tareaRepository
        .getTareasByCampania(campaniaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleCompletada(tarea: Tarea) {
        viewModelScope.launch {
            confirmarTareaUseCase(tarea.id, !tarea.confirmar).collect { }
        }
    }
}
