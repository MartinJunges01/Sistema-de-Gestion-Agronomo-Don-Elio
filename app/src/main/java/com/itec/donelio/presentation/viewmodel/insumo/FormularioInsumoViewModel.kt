package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.use_case.CrearInsumoCatalogoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FormularioInsumoState(
    val nombre: String = "",
    val categoria: String = "",
    val unidad: String = "",
    val isLoading: Boolean = false,
    val errorNombre: String? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class FormularioInsumoViewModel @Inject constructor(
    private val crearInsumoCatalogoUseCase: CrearInsumoCatalogoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FormularioInsumoState())
    val state: StateFlow<FormularioInsumoState> = _state.asStateFlow()

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, errorNombre = null) }
    }

    fun onCategoriaChange(value: String) {
        _state.update { it.copy(categoria = value) }
    }

    fun onUnidadChange(value: String) {
        _state.update { it.copy(unidad = value) }
    }

    fun guardar() {
        val current = _state.value
        if (current.nombre.isBlank()) {
            _state.update { it.copy(errorNombre = "El nombre es obligatorio") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                crearInsumoCatalogoUseCase(
                    nombre = current.nombre.trim(),
                    categoria = current.categoria.trim(),
                    unidad = current.unidad.trim()
                )
                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorNombre = e.message) }
            }
        }
    }
}
