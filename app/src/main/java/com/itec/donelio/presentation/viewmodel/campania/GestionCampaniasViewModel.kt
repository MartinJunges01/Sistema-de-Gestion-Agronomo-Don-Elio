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

@HiltViewModel
class GestionCampaniasViewModel @Inject constructor(
    private val obtenerCampaniasActivasUseCase: ObtenerCampaniasActivasUseCase,
    private val obtenerCampaniasInactivasUseCase: ObtenerCampaniasInactivasUseCase
) : ViewModel() {

    val campaniasActivas: StateFlow<List<Campania>> = obtenerCampaniasActivasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val campaniasInactivas: StateFlow<List<Campania>> = obtenerCampaniasInactivasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
