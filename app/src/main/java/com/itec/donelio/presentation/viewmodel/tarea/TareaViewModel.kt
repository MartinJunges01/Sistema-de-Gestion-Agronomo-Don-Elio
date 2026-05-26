package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.use_case.ConfirmarTareaUseCase
import com.itec.donelio.domain.use_case.EditarTareaUseCase
import com.itec.donelio.domain.use_case.EliminarTareaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerTareasDelDiaUseCase
import com.itec.donelio.domain.use_case.ObtenerTareasPorCampaniaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerTareasPorCampaniaUseCase: ObtenerTareasPorCampaniaUseCase,
    private val obtenerTareasDelDiaUseCase: ObtenerTareasDelDiaUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val confirmarTareaUseCase: ConfirmarTareaUseCase,
    private val editarTareaUseCase: EditarTareaUseCase,
    private val eliminarTareaUseCase: EliminarTareaUseCase
) : ViewModel() {

    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 })
    val campaniaIdSeleccionada = _campaniaIdSeleccionada.asStateFlow()

    private val _fechaSeleccionada = MutableStateFlow<Long>(System.currentTimeMillis())
    val fechaSeleccionada = _fechaSeleccionada.asStateFlow()

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tareas: StateFlow<List<Tarea>> = combine(_campaniaIdSeleccionada, _fechaSeleccionada) { id, fecha ->
        id to fecha
    }.flatMapLatest { (id, fecha) ->
        if (id != null) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = fecha
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            obtenerTareasDelDiaUseCase(id, cal.timeInMillis)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCampania(id: Int) {
        _campaniaIdSeleccionada.value = id
    }

    fun seleccionarFecha(fecha: Long) {
        _fechaSeleccionada.value = fecha
    }

    fun toggleCompletada(tarea: Tarea) {
        viewModelScope.launch {
            confirmarTareaUseCase(tarea.id, !tarea.confirmar).collect { }
        }
    }

    fun editarTarea(tarea: Tarea) {
        viewModelScope.launch {
            editarTareaUseCase(tarea).collect { }
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            eliminarTareaUseCase(tarea).collect { }
        }
    }
}
