package com.itec.donelio.presentation.viewmodel.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.core.util.DataSeeder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Optional
import javax.inject.Inject

sealed class SeedState {
    data object Idle : SeedState()
    data object Cargando : SeedState()
    data object Exito : SeedState()
    data class Error(val mensaje: String) : SeedState()
}

@HiltViewModel
class ConfiguracionDBViewModel @Inject constructor(
    private val dataSeeder: Optional<DataSeeder>
) : ViewModel() {

    private val _seedState = MutableStateFlow<SeedState>(SeedState.Idle)
    val seedState: StateFlow<SeedState> = _seedState.asStateFlow()

    fun cargarDatosPrueba() {
        viewModelScope.launch {
            _seedState.value = SeedState.Cargando
            try {
                if (dataSeeder.isPresent) {
                    dataSeeder.get().seedData()
                }
                _seedState.value = SeedState.Exito
            } catch (e: Exception) {
                _seedState.value = SeedState.Error(e.localizedMessage ?: "Error al cargar datos de prueba")
            }
        }
    }

    fun resetSeedState() {
        _seedState.value = SeedState.Idle
    }
}
