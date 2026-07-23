package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val isCampaniaValid: StateFlow<Boolean> = _campaniaIdSeleccionada
        .map { it != null && it != -1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _fechaSeleccionada = MutableStateFlow<Long>(System.currentTimeMillis())
    val fechaSeleccionada = _fechaSeleccionada.asStateFlow()

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tareas: StateFlow<List<Tarea>> = combine(_campaniaIdSeleccionada, _fechaSeleccionada) { id, fecha ->
        id to fecha
    }.flatMapLatest { (id, fecha) ->
        if (id != null && id != -1) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = fecha
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            obtenerTareasDelDiaUseCase(id, cal.timeInMillis)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCampania(id: Int) { _campaniaIdSeleccionada.value = id }
    fun seleccionarFecha(fecha: Long) { _fechaSeleccionada.value = fecha }
    fun clearError() { _errorMessage.value = null }

    /**
     * Sincroniza el [campaniaId] externo con el estado interno del ViewModel.
     * Se utiliza cuando el ViewModel es reutilizado desde distintas pestañas de Detalle de Campaña
     * para evitar que queden datos cacheados de la campaña anterior.
     * Solo emite si el valor difiere del actual, evitando actualizaciones innecesarias del StateFlow.
     *
     * @param id Identificador de la campaña actualmente visible en pantalla.
     */
    fun sincronizarCampania(id: Int) {
        if (_campaniaIdSeleccionada.value != id) {
            _campaniaIdSeleccionada.value = id
        }
    }

    fun toggleCompletada(tarea: Tarea) {
        viewModelScope.launch {
            confirmarTareaUseCase(tarea.id, !tarea.confirmar)
                .catch { _errorMessage.value = "Error al actualizar estado de tarea" }
                .collect()
        }
    }

    fun editarTarea(tarea: Tarea) {
        viewModelScope.launch {
            editarTareaUseCase(tarea)
                .catch { _errorMessage.value = "Error al editar tarea" }
                .collect()
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            eliminarTareaUseCase(tarea)
                .catch { _errorMessage.value = "Error al eliminar tarea" }
                .collect()
        }
    }
}
