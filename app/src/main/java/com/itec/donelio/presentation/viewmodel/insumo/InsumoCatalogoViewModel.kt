package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InsumoCatalogoViewModel @Inject constructor(
    private val obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase
) : ViewModel() {

    val catalogo: StateFlow<List<Insumo>> = obtenerCatalogoInsumosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
