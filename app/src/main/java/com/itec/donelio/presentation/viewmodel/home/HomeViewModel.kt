package com.itec.donelio.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase
) : ViewModel() {

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
