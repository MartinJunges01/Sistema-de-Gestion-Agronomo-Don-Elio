package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.AsignarInsumoACampaniaUseCase
import com.itec.donelio.domain.use_case.DesvincularInsumoUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import com.itec.donelio.domain.use_case.ObtenerInsumosVinculadosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsumoVinculacionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerInsumosVinculadosUseCase: ObtenerInsumosVinculadosUseCase,
    private val obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase,
    private val asignarInsumoACampaniaUseCase: AsignarInsumoACampaniaUseCase,
    private val desvincularInsumoUseCase: DesvincularInsumoUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase
) : ViewModel() {

    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 })
    val campaniaIdSeleccionada = _campaniaIdSeleccionada.asStateFlow()

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val insumosVinculados: StateFlow<List<CampaniaInsumo>> = _campaniaIdSeleccionada.flatMapLatest { id ->
        if (id != null) obtenerInsumosVinculadosUseCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCampania(id: Int) {
        _campaniaIdSeleccionada.value = id
    }

    val catalogo: StateFlow<List<Insumo>> = obtenerCatalogoInsumosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun asignarInsumo(idInsumo: Int, cantidad: Double, precio: Double) {
        viewModelScope.launch {
            val campaniaId = _campaniaIdSeleccionada.value ?: return@launch
            try {
                asignarInsumoACampaniaUseCase(campaniaId, idInsumo, cantidad, precio)
            } catch (_: Exception) { }
        }
    }

    fun desvincularInsumo(campaniaInsumo: CampaniaInsumo) {
        viewModelScope.launch {
            try {
                desvincularInsumoUseCase(campaniaInsumo)
            } catch (_: Exception) { }
        }
    }
}
