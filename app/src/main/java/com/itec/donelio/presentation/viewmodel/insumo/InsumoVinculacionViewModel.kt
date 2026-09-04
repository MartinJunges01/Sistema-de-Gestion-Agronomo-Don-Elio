package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsumoVinculacionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ultimaSeleccionManager: com.itec.donelio.presentation.state.UltimaSeleccionManager,
    private val obtenerInsumosVinculadosUseCase: ObtenerInsumosVinculadosUseCase,
    private val obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase,
    private val asignarInsumoACampaniaUseCase: AsignarInsumoACampaniaUseCase,
    private val desvincularInsumoUseCase: DesvincularInsumoUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase
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
        
        // Si hay una campania seteada desde el nav arg, actualizamos el manager global
        _campaniaIdSeleccionada.value?.let { 
            ultimaSeleccionManager.seleccionarCampania(it) 
        }
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val isCampaniaValid: StateFlow<Boolean> = _campaniaIdSeleccionada
        .map { it != null && it != -1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val insumosVinculados: StateFlow<List<CampaniaInsumo>> = _campaniaIdSeleccionada.flatMapLatest { id ->
        if (id != null && id != -1) obtenerInsumosVinculadosUseCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val catalogo: StateFlow<List<Insumo>> = obtenerCatalogoInsumosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCampania(id: Int) {
        _campaniaIdSeleccionada.value = id
        ultimaSeleccionManager.seleccionarCampania(id)
    }

    fun sincronizarInsumos(campaniaId: Int) {
        if (_campaniaIdSeleccionada.value != campaniaId) {
            _campaniaIdSeleccionada.value = campaniaId
        }
    }

    fun clearError() { _errorMessage.value = null }

    fun asignarInsumo(idInsumo: Int, cantidad: Double, precio: Double) {
        val campaniaId = _campaniaIdSeleccionada.value ?: return
        viewModelScope.launch {
            try {
                asignarInsumoACampaniaUseCase(campaniaId, idInsumo, cantidad, precio)
            } catch (e: Exception) {
                _errorMessage.value = "Error al vincular insumo: ${e.message}"
            }
        }
    }

    fun desvincularInsumo(campaniaInsumo: CampaniaInsumo) {
        viewModelScope.launch {
            try {
                desvincularInsumoUseCase(campaniaInsumo)
            } catch (e: Exception) {
                _errorMessage.value = "Error al desvincular insumo: ${e.message}"
            }
        }
    }
}
