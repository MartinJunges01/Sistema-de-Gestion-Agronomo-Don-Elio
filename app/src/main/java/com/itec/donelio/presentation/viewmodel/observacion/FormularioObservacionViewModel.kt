package com.itec.donelio.presentation.viewmodel.observacion

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.use_case.GuardarObservacionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FormularioObservacionState(
    val texto: String = "",
    val imagenUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorTexto: String? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class FormularioObservacionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val guardarObservacionUseCase: GuardarObservacionUseCase
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1

    private val _state = MutableStateFlow(FormularioObservacionState())
    val state: StateFlow<FormularioObservacionState> = _state.asStateFlow()

    fun onTextoChange(value: String) {
        _state.update { it.copy(texto = value, errorTexto = null) }
    }

    fun onImagenSeleccionada(uri: Uri?) {
        _state.update { it.copy(imagenUri = uri) }
    }

    fun onBorrarImagen() {
        _state.update { it.copy(imagenUri = null) }
    }

    fun guardar() {
        val current = _state.value
        if (current.texto.isBlank()) {
            _state.update { it.copy(errorTexto = "El texto no puede estar vacío") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val internalPath = current.imagenUri?.let { copyImageToInternalStorage(it) }

                guardarObservacionUseCase(
                    texto = current.texto.trim(),
                    imagenUri = internalPath,
                    idCampania = campaniaId
                )
                _state.update { it.copy(isLoading = false, guardadoExitoso = true, texto = "", imagenUri = null) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorTexto = e.localizedMessage) }
            }
        }
    }

    fun resetGuardadoExitoso() {
        _state.update { it.copy(guardadoExitoso = false) }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "obs_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            val outputStream = java.io.FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
