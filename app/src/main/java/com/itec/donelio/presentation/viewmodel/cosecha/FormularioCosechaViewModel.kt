package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.use_case.EditarCosechaConVentaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechaPorIdUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaConVentaUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaUseCase
import com.itec.donelio.domain.use_case.ValidarDatosCosechaUseCase
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import com.itec.donelio.presentation.state.UltimaSeleccionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val errorFecha: String? = null,
    val errorPrecio: String? = null,
    val errorCampania: String? = null,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null
)

@HiltViewModel
class FormularioCosechaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val registrarCosechaUseCase: RegistrarCosechaUseCase,
    private val registrarConVentaUseCase: RegistrarCosechaConVentaUseCase,
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val obtenerCosechaPorIdUseCase: ObtenerCosechaPorIdUseCase,
    private val editarCosechaConVentaUseCase: EditarCosechaConVentaUseCase,
    private val validarDatosCosechaUseCase: ValidarDatosCosechaUseCase,
    private val cosechaNoAlmacenadaRepository: CosechaNoAlmacenadaRepository,
    private val ultimaSeleccionManager: UltimaSeleccionManager
) : ViewModel() {

    private val initialCampaniaId = savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 }
    private val initialCosechaId = savedStateHandle.get<Int>("cosechaId").takeIf { it != -1 }

    private val _state = MutableStateFlow(
        FormularioCosechaState(campaniaId = initialCampaniaId, cosechaId = initialCosechaId)
    )
    val state: StateFlow<FormularioCosechaState> = _state.asStateFlow()

    init {
        if (initialCosechaId != null) {
            cargarCosecha(initialCosechaId)
        } else if (initialCampaniaId == null) {
            // Fallback: si no se navegó con un campaniaId explícito, usar el Singleton
            viewModelScope.launch {
                val idDelManager = ultimaSeleccionManager.campaniaIdSeleccionada.first()
                if (idDelManager != null) {
                    _state.update { it.copy(campaniaId = idDelManager) }
                }
            }
        }
    }

    private fun cargarCosecha(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val cosecha = obtenerCosechaPorIdUseCase(id)
            if (cosecha != null) {
                val esAlmacenada = cosecha.almacen.isNotBlank()
                // Si es una venta/reserva, buscar los detalles de la tabla secundaria
                val detalle = if (!esAlmacenada) {
                    cosechaNoAlmacenadaRepository.getPorCosechaId(cosecha.id)
                } else null

                _state.update {
                    it.copy(
                        isLoading = false,
                        cantidad = cosecha.cantidad.toString(),
                        fecha = cosecha.fecha,
                        almacen = cosecha.almacen,
                        almacenado = esAlmacenada,
                        campaniaId = cosecha.idCampania,
                        tipo = detalle?.tipo ?: "",
                        precio = if (detalle?.precio != null && detalle.precio > 0) detalle.precio.toString() else ""
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
        // Normalizar: reemplazar coma por punto para aceptar ambos separadores decimales
        val normalizado = value.replace(",", ".")
        val error = if (normalizado.isNotBlank() && (normalizado.toDoubleOrNull() == null || normalizado.toDouble() <= 0))
            "Cantidad inválida" else null
        _state.update { it.copy(cantidad = normalizado, errorCantidad = error) }
    }

    fun onFechaChange(timestamp: Long) { _state.update { it.copy(fecha = timestamp, errorFecha = null) } }
    fun onAlmacenChange(value: String) { _state.update { it.copy(almacen = value) } }
    fun onTipoChange(value: String) { _state.update { it.copy(tipo = value) } }

    fun onPrecioChange(value: String) {
        // Normalizar: reemplazar coma por punto para aceptar ambos separadores decimales
        val normalizado = value.replace(",", ".")
        val error = if (normalizado.isNotBlank() && normalizado.toDoubleOrNull() == null) "Precio inválido" else null
        _state.update { it.copy(precio = normalizado, errorPrecio = error) }
    }

    fun onCampaniaChange(id: Int) { _state.update { it.copy(campaniaId = id, errorCampania = null) } }

    fun guardar() {
        val current = _state.value

        if (current.campaniaId == null) {
            _state.update { it.copy(errorCampania = "Debe seleccionar una campaña") }
            return
        }

        val cantidadDouble = current.cantidad.toDoubleOrNull()
        val validacion = validarDatosCosechaUseCase(
            cantidad = cantidadDouble,
            fecha = current.fecha,
            isAlmacenada = current.almacenado,
            almacen = current.almacen
        )

        if (validacion is com.itec.donelio.domain.util.ValidationResult.Error) {
            val msg = validacion.message
            when {
                msg.contains("cantidad", ignoreCase = true) ->
                    _state.update { it.copy(errorCantidad = msg) }
                msg.contains("fecha", ignoreCase = true) ->
                    _state.update { it.copy(errorFecha = msg) }
                else ->
                    _state.update { it.copy(errorGeneral = msg) }
            }
            return
        }

        if (current.errorPrecio != null) return

        val campaniaId = current.campaniaId
        val cantidad = cantidadDouble ?: 0.0

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (current.cosechaId != null) {
                    // Modo edición: usar el UseCase que actualiza ambas tablas
                    val cosechaEditada = Cosecha(
                        id = current.cosechaId,
                        idCampania = campaniaId,
                        cantidad = cantidad,
                        fecha = current.fecha,
                        almacen = if (current.almacenado) current.almacen.trim() else ""
                    )
                    editarCosechaConVentaUseCase(
                        cosecha = cosechaEditada,
                        esAlmacenada = current.almacenado,
                        tipo = current.tipo.trim(),
                        precioTotal = current.precio.toDoubleOrNull() ?: 0.0
                    )
                } else {
                    // Modo creación
                    if (current.almacenado) {
                        registrarCosechaUseCase(cantidad, current.fecha, current.almacen.trim(), campaniaId)
                    } else {
                        registrarConVentaUseCase(
                            cantidad, current.fecha, campaniaId,
                            current.tipo.trim(),
                            current.precio.toDoubleOrNull() ?: 0.0
                        )
                    }
                }
                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                val msg = e.message ?: "Error al guardar"
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorCantidad = if (msg.contains("cantidad", ignoreCase = true)) msg else null,
                        errorGeneral = if (!msg.contains("cantidad", ignoreCase = true)) msg else null
                    )
                }
            }
        }
    }

    fun limpiarErrorGeneral() {
        _state.update { it.copy(errorGeneral = null) }
    }
}
