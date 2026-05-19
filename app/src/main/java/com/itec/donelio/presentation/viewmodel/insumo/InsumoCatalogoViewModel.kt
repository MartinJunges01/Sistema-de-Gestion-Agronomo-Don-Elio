package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.EditarInsumoCatalogoUseCase
import com.itec.donelio.domain.use_case.EliminarInsumoCatalogoUseCase
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsumoCatalogoViewModel @Inject constructor(
    private val obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase,
    private val editarInsumoCatalogoUseCase: EditarInsumoCatalogoUseCase,
    private val eliminarInsumoCatalogoUseCase: EliminarInsumoCatalogoUseCase
) : ViewModel() {

    val catalogo: StateFlow<List<Insumo>> = obtenerCatalogoInsumosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun editarInsumo(insumo: Insumo) {
        viewModelScope.launch {
            try {
                editarInsumoCatalogoUseCase(insumo)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error al editar insumo"
            }
        }
    }

    fun eliminarInsumo(insumo: Insumo) {
        viewModelScope.launch {
            try {
                eliminarInsumoCatalogoUseCase(insumo)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error al eliminar insumo"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
