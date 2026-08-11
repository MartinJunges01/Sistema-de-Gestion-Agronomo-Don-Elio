package com.itec.donelio.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.use_case.ObtenerCampaniasActivasUseCase
import com.itec.donelio.domain.use_case.ObtenerTareasPendientesUseCase
import com.itec.donelio.core.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obtenerCampaniasActivasUseCase: ObtenerCampaniasActivasUseCase,
    private val obtenerTareasPendientesUseCase: ObtenerTareasPendientesUseCase,
    sessionManager: SessionManager
) : ViewModel() {

    val userName: StateFlow<String> = sessionManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Invitado")

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasActivasUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Filtro: hoy - 7 días a medianoche
    private val fechaLimite: Long get() {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -7)
        return cal.timeInMillis
    }

    val tareasPendientes: StateFlow<List<Tarea>> = obtenerTareasPendientesUseCase(limite = 5, fechaLimite = fechaLimite)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
