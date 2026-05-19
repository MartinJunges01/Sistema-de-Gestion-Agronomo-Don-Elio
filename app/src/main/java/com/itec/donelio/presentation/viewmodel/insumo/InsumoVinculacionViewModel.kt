package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import com.itec.donelio.domain.use_case.AsignarInsumoACampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsumoVinculacionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val campaniaInsumoRepository: CampaniaInsumoRepository,
    private val obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase,
    private val asignarInsumoACampaniaUseCase: AsignarInsumoACampaniaUseCase
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1

    val insumosVinculados: StateFlow<List<CampaniaInsumo>> = campaniaInsumoRepository
        .getInsumosUtilizadosEnCampania(campaniaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val catalogo: StateFlow<List<Insumo>> = obtenerCatalogoInsumosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun asignarInsumo(idInsumo: Int, cantidad: Double, precio: Double) {
        viewModelScope.launch {
            try {
                asignarInsumoACampaniaUseCase(campaniaId, idInsumo, cantidad, precio)
            } catch (_: Exception) { }
        }
    }
}
