package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.CrearInsumoCatalogoUseCase
import com.itec.donelio.domain.use_case.EditarInsumoCatalogoUseCase
import com.itec.donelio.domain.use_case.ObtenerInsumoPorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.itec.donelio.domain.use_case.ValidarInsumoUseCase

data class FormularioInsumoState(
    val nombre: String = "",
    val categoria: String = "",
    val icono: String? = null,
    val isLoading: Boolean = false,
    val errorNombre: String? = null,
    val errorCategoria: String? = null,
    val isGuardarHabilitado: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null
)

@HiltViewModel
class FormularioInsumoViewModel @Inject constructor(
    private val crearInsumoCatalogoUseCase: CrearInsumoCatalogoUseCase,
    private val editarInsumoCatalogoUseCase: EditarInsumoCatalogoUseCase,
    private val obtenerInsumoPorIdUseCase: ObtenerInsumoPorIdUseCase,
    private val validarInsumoUseCase: ValidarInsumoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val insumoId: Int? = savedStateHandle.get<Int>("insumoId")?.takeIf { it != -1 }

    private val _state = MutableStateFlow(FormularioInsumoState())
    val state: StateFlow<FormularioInsumoState> = _state.asStateFlow()

    init {
        insumoId?.let { cargarInsumo(it) }
    }

    private fun cargarInsumo(id: Int) {
        viewModelScope.launch {
            val insumo = obtenerInsumoPorIdUseCase(id)
            insumo?.let {
                _state.update { state ->
                    state.copy(
                        nombre = it.nombre,
                        categoria = it.categoria,
                        icono = it.icono
                    )
                }
                evaluarValidaciones(it.nombre, it.categoria)
            }
        }
    }

    private fun evaluarValidaciones(nombre: String, categoria: String) {
        val resultado = validarInsumoUseCase(nombre, categoria)
        _state.update { it.copy(
            errorNombre = resultado.errorNombre,
            errorCategoria = resultado.errorCategoria,
            isGuardarHabilitado = resultado.esValido
        )}
    }

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, errorNombre = null) }
    }

    fun onCategoriaChange(value: String) {
        _state.update { it.copy(categoria = value, errorCategoria = null) }
    }

    fun onIconoChange(value: String?) {
        _state.update { it.copy(icono = value) }
    }

    fun guardar() {
        val current = _state.value
        val validacion = validarInsumoUseCase(current.nombre, current.categoria)
        
        if (!validacion.esValido) {
            _state.update { it.copy(
                errorNombre = validacion.errorNombre,
                errorCategoria = validacion.errorCategoria
            )}
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (insumoId != null) {
                    editarInsumoCatalogoUseCase(
                        Insumo(
                            id = insumoId,
                            nombre = current.nombre.trim(),
                            categoria = current.categoria.trim(),
                            icono = current.icono
                        )
                    )
                } else {
                    crearInsumoCatalogoUseCase(
                        nombre = current.nombre.trim(),
                        categoria = current.categoria.trim(),
                        icono = current.icono
                    )
                }
                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorGeneral = e.localizedMessage ?: "Error al guardar insumo") }
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(errorGeneral = null) }
    }
}
