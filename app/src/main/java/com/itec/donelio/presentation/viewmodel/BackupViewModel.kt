package com.itec.donelio.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.use_case.CrearBackupUseCase
import com.itec.donelio.domain.use_case.RestaurarBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BackupUiState {
    data object Idle : BackupUiState
    data object Loading : BackupUiState
    data class Success(val message: String) : BackupUiState
    data class Error(val message: String) : BackupUiState
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val crearBackupUseCase: CrearBackupUseCase,
    private val restaurarBackupUseCase: RestaurarBackupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _restoreCompleted = MutableStateFlow(false)
    val restoreCompleted: StateFlow<Boolean> = _restoreCompleted.asStateFlow()

    fun crearBackup(outputUri: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            val result = crearBackupUseCase(outputUri)
            _uiState.value = result.fold(
                onSuccess = { BackupUiState.Success("Respaldo creado exitosamente") },
                onFailure = { BackupUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun restaurarBackup(inputUri: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            val result = restaurarBackupUseCase(inputUri)
            _uiState.value = result.fold(
                onSuccess = {
                    _restoreCompleted.value = true
                    BackupUiState.Success("Datos restaurados. Reiniciando...")
                },
                onFailure = { BackupUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun resetState() {
        _uiState.value = BackupUiState.Idle
    }
}
