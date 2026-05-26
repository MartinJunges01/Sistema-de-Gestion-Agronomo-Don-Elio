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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CosechaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerCosechasPorCampaniaUseCase: ObtenerCosechasPorCampaniaUseCase,
    private val obtenerCosechasNoAlmacenadasUseCase: ObtenerCosechasNoAlmacenadasUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase
) : ViewModel() {

    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 })
    val campaniaIdSeleccionada = _campaniaIdSeleccionada.asStateFlow()

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cosechas: StateFlow<List<Cosecha>> = _campaniaIdSeleccionada.flatMapLatest { id ->
        if (id != null) obtenerCosechasPorCampaniaUseCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCampania(id: Int) {
        _campaniaIdSeleccionada.value = id
    }

    val almacenadas: StateFlow<List<Cosecha>> = cosechas
        .map { list -> list.filter { it.almacen.isNotBlank() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val noAlmacenadasDetalle: StateFlow<Map<Int, CosechaNoAlmacenada>> =
        _campaniaIdSeleccionada.flatMapLatest { id ->
            if (id != null) obtenerCosechasNoAlmacenadasUseCase(id) else flowOf(emptyMap())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
