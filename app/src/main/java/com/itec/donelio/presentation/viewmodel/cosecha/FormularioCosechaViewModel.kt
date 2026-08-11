package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechaPorIdUseCase
import com.itec.donelio.domain.use_case.EditarCosechaUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaConVentaUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    val campaniaId: Int? = null,
    val cosechaId: Int? = null,
    val isLoading: Boolean = false,
    val errorCantidad: String? = null,
    val errorPrecio: String? = null,
    val errorCampania: String? = null,
    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class FormularioCosechaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val registrarCosechaUseCase: RegistrarCosechaUseCase,
    private val registrarConVentaUseCase: RegistrarCosechaConVentaUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val obtenerCosechaPorIdUseCase: ObtenerCosechaPorIdUseCase,
    private val editarCosechaUseCase: EditarCosechaUseCase
) : ViewModel() {

    private val initialCampaniaId = savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 }
    private val initialCosechaId = savedStateHandle.get<Int>("cosechaId").takeIf { it != -1 }

    private val _state = MutableStateFlow(FormularioCosechaState(campaniaId = initialCampaniaId, cosechaId = initialCosechaId))
    val state: StateFlow<FormularioCosechaState> = _state.asStateFlow()

    init {
        if (initialCosechaId != null) {
            cargarCosecha(initialCosechaId)
        }
    }

    private fun cargarCosecha(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val cosecha = obtenerCosechaPorIdUseCase(id)
            if (cosecha != null) {
                // Si es almacenada (almacen no es blank) o venta (almacen en blanco pero sin detalle - esto lo asume la vista)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        cantidad = cosecha.cantidad.toString(),
                        fecha = cosecha.fecha,
                        almacen = cosecha.almacen,
                        almacenado = cosecha.almacen.isNotBlank(),
                        campaniaId = cosecha.idCampania
                        // NOTA: Para ventas/no almacenadas, en un futuro se debería cargar el detalle de CosechaNoAlmacenada
                        // Por simplicidad del bug, el form asume almacenada o blanquea si no.
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, errorCantidad = "Cosecha no encontrada") }
            }
        }
    }

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
    fun onCampaniaChange(id: Int) { _state.update { it.copy(campaniaId = id, errorCampania = null) } }

    fun guardar() {
        val current = _state.value

        if (current.campaniaId == null) {
            _state.update { it.copy(errorCampania = "Debe seleccionar una campaña") }
            return
        }

        if (current.cantidad.isBlank()) {
            _state.update { it.copy(errorCantidad = "La cantidad es obligatoria") }
            return
        }

        if (current.errorCantidad != null || current.errorPrecio != null) return

        val campaniaId = current.campaniaId
        val cantidad = current.cantidad.toDouble()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (current.cosechaId != null) {
                    val cosechaEditada = com.itec.donelio.domain.model.Cosecha(
                        id = current.cosechaId,
                        idCampania = campaniaId,
                        cantidad = cantidad,
                        fecha = current.fecha,
                        almacen = if (current.almacenado) current.almacen.trim() else ""
                    )
                    editarCosechaUseCase(cosechaEditada)
                } else {
                    if (current.almacenado) {
                        registrarCosechaUseCase(cantidad, current.fecha, current.almacen.trim(), campaniaId)
                    } else {
                        registrarConVentaUseCase(cantidad, current.fecha, campaniaId, current.tipo.trim(), current.precio.toDoubleOrNull() ?: 0.0)
                    }
                }
                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorCantidad = e.message ?: "Error al guardar") }
            }
        }
    }
}
