package com.itec.donelio.presentation.viewmodel.cultivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.use_case.CrearCultivoUseCase
import com.itec.donelio.domain.use_case.EditarCultivoUseCase
import com.itec.donelio.domain.use_case.EliminarCultivoUseCase
import com.itec.donelio.domain.use_case.ObtenerCultivosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CultivoCatalogoViewModel @Inject constructor(
    private val obtenerCultivosUseCase: ObtenerCultivosUseCase,
    private val crearCultivoUseCase: CrearCultivoUseCase,
    private val editarCultivoUseCase: EditarCultivoUseCase,
    private val eliminarCultivoUseCase: EliminarCultivoUseCase
) : ViewModel() {

    // Obtenemos todos los cultivos (incluyendo los que puedan estar inactivos para gestión si se quiere, 
    // pero la regla dice que en el selector solo activos. En el catálogo mostramos los activos).
    val catalogo: StateFlow<List<Cultivo>> = obtenerCultivosUseCase(soloActivos = true)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun crearCultivo(nombre: String) {
        viewModelScope.launch {
            try {
                crearCultivoUseCase(nombre)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error al crear cultivo"
            }
        }
    }

    fun editarCultivo(cultivo: Cultivo) {
        viewModelScope.launch {
            try {
                editarCultivoUseCase(cultivo)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error al editar cultivo"
            }
        }
    }

    fun eliminarCultivo(id: Int) {
        viewModelScope.launch {
            try {
                eliminarCultivoUseCase(id)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error al eliminar cultivo"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun validarCultivo(nombre: String): Boolean {
        return nombre.isNotBlank()
    }
}
