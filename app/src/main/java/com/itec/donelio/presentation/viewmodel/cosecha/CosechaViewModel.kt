package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechasNoAlmacenadasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechasPorCampaniaUseCase
import com.itec.donelio.domain.use_case.EliminarCosechaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CosechaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ultimaSeleccionManager: com.itec.donelio.presentation.state.UltimaSeleccionManager,
    private val obtenerCosechasPorCampaniaUseCase: ObtenerCosechasPorCampaniaUseCase,
    private val obtenerCosechasNoAlmacenadasUseCase: ObtenerCosechasNoAlmacenadasUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val eliminarCosechaUseCase: EliminarCosechaUseCase
) : ViewModel() {

    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 })
    val campaniaIdSeleccionada = _campaniaIdSeleccionada.asStateFlow()

    init {
        viewModelScope.launch {
            ultimaSeleccionManager.campaniaIdSeleccionada.collect { id ->
                if (id != null && _campaniaIdSeleccionada.value != id) {
                    _campaniaIdSeleccionada.value = id
                }
            }
        }
        _campaniaIdSeleccionada.value?.let { 
            ultimaSeleccionManager.seleccionarCampania(it) 
        }
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _cosechaAEliminar = MutableStateFlow<Cosecha?>(null)
    val cosechaAEliminar = _cosechaAEliminar.asStateFlow()

    val isCampaniaValid: StateFlow<Boolean> = _campaniaIdSeleccionada
        .map { it != null && it != -1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cosechas: StateFlow<List<Cosecha>> = _campaniaIdSeleccionada.flatMapLatest { id ->
        if (id != null && id != -1) obtenerCosechasPorCampaniaUseCase(id) else flowOf(emptyList())
    }.catch { _errorMessage.value = "Error al cargar cosechas" }
     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCampania(id: Int) { 
        _campaniaIdSeleccionada.value = id 
        ultimaSeleccionManager.seleccionarCampania(id)
    }
    fun clearError() { _errorMessage.value = null }

    fun solicitarEliminacion(cosecha: Cosecha) {
        _cosechaAEliminar.value = cosecha
    }

    fun cancelarEliminacion() {
        _cosechaAEliminar.value = null
    }

    fun confirmarEliminacion() {
        val cosecha = _cosechaAEliminar.value ?: return
        viewModelScope.launch {
            try {
                eliminarCosechaUseCase(cosecha)
                _cosechaAEliminar.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar cosecha: ${e.message}"
            }
        }
    }

    val almacenadas: StateFlow<List<Cosecha>> = cosechas
        .map { list -> list.filter { it.almacen.isNotBlank() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val noAlmacenadasDetalle: StateFlow<Map<Int, CosechaNoAlmacenada>> =
        _campaniaIdSeleccionada.flatMapLatest { id ->
            if (id != null && id != -1) obtenerCosechasNoAlmacenadasUseCase(id) else flowOf(emptyMap())
        }.catch { _errorMessage.value = "Error al cargar detalle de cosechas" }
         .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
