package com.itec.donelio.presentation.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UltimaSeleccionManager @Inject constructor() {
    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(null)
    val campaniaIdSeleccionada: StateFlow<Int?> = _campaniaIdSeleccionada.asStateFlow()

    fun seleccionarCampania(id: Int) {
        _campaniaIdSeleccionada.value = id
    }
}
