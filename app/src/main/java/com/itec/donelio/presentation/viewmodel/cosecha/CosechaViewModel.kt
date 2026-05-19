package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CosechaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cosechaRepository: CosechaRepository,
    private val noAlmacenadaRepository: CosechaNoAlmacenadaRepository
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1

    val cosechas: StateFlow<List<Cosecha>> = cosechaRepository
        .getCosechasByCampania(campaniaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val almacenadas: StateFlow<List<Cosecha>> = cosechas
        .map { list -> list.filter { it.almacen.isNotBlank() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val noAlmacenadasDetalle: StateFlow<Map<Int, CosechaNoAlmacenada>> =
        noAlmacenadaRepository.getNoAlmacenadasPorCampania(campaniaId)
            .map { lista -> lista.associateBy { it.idCosecha } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
