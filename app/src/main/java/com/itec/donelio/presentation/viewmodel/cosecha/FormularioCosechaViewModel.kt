package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.use_case.RegistrarCosechaConVentaUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FormularioCosechaState(
    val almacenado: Boolean = true,
    val cantidad: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val almacen: String = "",
    val tipo: String = "",
    val precio: String = "",
    val isLoading: Boolean = false,
    val errorCantidad: String? = null,
    val errorPrecio: String? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class FormularioCosechaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val registrarCosechaUseCase: RegistrarCosechaUseCase,
    private val registrarConVentaUseCase: RegistrarCosechaConVentaUseCase
) : ViewModel() {

    private val campaniaId: Int = savedStateHandle.get<Int>("campaniaId") ?: -1
    private val _state = MutableStateFlow(FormularioCosechaState())
    val state: StateFlow<FormularioCosechaState> = _state.asStateFlow()

    fun onAlmacenadoChange(value: Boolean) { _state.update { it.copy(almacenado = value) } }
    fun onCantidadChange(value: String) {
        val error = if (value.isNotBlank() && (value.toDoubleOrNull() == null || value.toDouble() <= 0)) 
            "Cantidad inválida" else null
        _state.update { it.copy(cantidad = value, errorCantidad = error) }
    }
    fun onFechaChange(timestamp: Long) { _state.update { it.copy(fecha = timestamp) } }
    fun onAlmacenChange(value: String) { _state.update { it.copy(almacen = value) } }
    fun onTipoChange(value: String) { _state.update { it.copy(tipo = value) } }
    fun onPrecioChange(value: String) {
        val error = if (value.isNotBlank() && value.toDoubleOrNull() == null) "Precio inválido" else null
        _state.update { it.copy(precio = value, errorPrecio = error) }
    }

    fun guardar() {
        val current = _state.value
        if (current.errorCantidad != null || current.errorPrecio != null || current.cantidad.isBlank()) return

        val cantidad = current.cantidad.toDouble()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (current.almacenado) {
                    registrarCosechaUseCase(cantidad, current.fecha, current.almacen.trim(), campaniaId)
                } else {
                    registrarConVentaUseCase(cantidad, current.fecha, campaniaId, current.tipo.trim(), current.precio.toDoubleOrNull() ?: 0.0)
                }
                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorCantidad = e.message ?: "Error al guardar") }
            }
        }
    }
}
