package com.itec.donelio.presentation.viewmodel.reportes

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.itec.donelio.core.utils.ReportExporter
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.InsumoResumen
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechasPorCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerInsumosVinculadosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de Reportes y Análisis.
 *
 * Expone dos secciones diferenciadas:
 * - **Sección 1 — Estadísticas individuales:** el usuario selecciona una campaña y ve
 *   sus métricas reales (insumos, cosechas, PieChart de distribución).
 * - **Sección 2 — Comparador:** el usuario selecciona dos campañas (A y B) y compara
 *   el costo total de insumos entre ellas.
 *
 * La exportación (CSV/PDF) usa los datos de la campaña seleccionada en Sección 1.
 *
 * Nota de scope: la lógica de comparación de [campaniaA]/[campaniaB] es un paso
 * preparatorio del Issue #302 (Comparación Real entre Campañas).
 */
@HiltViewModel
class ReportesViewModel @Inject constructor(
    obtenerCampaniasUseCase: ObtenerCampaniasUseCase,
    private val obtenerInsumosVinculadosUseCase: ObtenerInsumosVinculadosUseCase,
    obtenerCosechasPorCampaniaUseCase: ObtenerCosechasPorCampaniaUseCase,
    obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase
) : ViewModel() {

    // ──────────────────────────────────────────────
    // Estado de exportación
    // ──────────────────────────────────────────────

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus

    fun clearExportStatus() { _exportStatus.value = null }

    // ──────────────────────────────────────────────
    // Lista de campañas para dropdowns
    // ──────────────────────────────────────────────

    val campanias: StateFlow<List<Campania>> = obtenerCampaniasUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ──────────────────────────────────────────────
    // Sección 1 — Estadísticas de campaña individual
    // ──────────────────────────────────────────────

    private val _campaniaIndividual = MutableStateFlow<Campania?>(null)
    val campaniaIndividual: StateFlow<Campania?> = _campaniaIndividual.asStateFlow()

    /** Selecciona la campaña para la Sección 1 (estadísticas + PieChart + exportación). */
    fun seleccionarCampaniaIndividual(campania: Campania) {
        _campaniaIndividual.value = campania
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val insumosIndividual: StateFlow<List<CampaniaInsumo>> = _campaniaIndividual
        .flatMapLatest { campania ->
            if (campania != null) obtenerInsumosVinculadosUseCase(campania.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cosechasIndividual: StateFlow<List<Cosecha>> = _campaniaIndividual
        .flatMapLatest { campania ->
            if (campania != null) obtenerCosechasPorCampaniaUseCase(campania.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val coloresInsumos = listOf(
        Color(0xFF15803d), Color(0xFF1d4ed8), Color(0xFFd97706),
        Color(0xFFb91c1c), Color(0xFF6b21a8), Color(0xFF0369a1), Color(0xFF4d7c0f)
    )

    /**
     * Resumen de insumos de la campaña seleccionada, con nombres resueltos desde el catálogo.
     * Alimenta tanto el [pieChartData] como la exportación CSV/PDF.
     */
    val exportableData: StateFlow<List<InsumoResumen>> = combine(
        insumosIndividual,
        obtenerCatalogoInsumosUseCase()
    ) { insumos, catalogo ->
        val catalogoMap = catalogo.associateBy { it.id }
        insumos.groupBy { it.idInsumo }
            .map { (idInsumo, entradas) ->
                val nombre = catalogoMap[idInsumo]?.nombre ?: "Desconocido"
                InsumoResumen(
                    nombreInsumo = nombre,
                    cantidadTotal = entradas.sumOf { it.cantidad },
                    costoTotal = entradas.sumOf { it.cantidad * it.precio }
                )
            }
            .filter { it.costoTotal > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Datos del PieChart de distribución de gastos por insumo.
     * Contextual a la campaña seleccionada en Sección 1.
     * Emite null cuando no hay campaña seleccionada o no hay insumos.
     */
    val pieChartData: StateFlow<PieChartData?> = exportableData
        .map { insumosResumen ->
            if (insumosResumen.isEmpty()) return@map null
            PieChartData(
                slices = insumosResumen.mapIndexed { index, insumo ->
                    PieChartData.Slice(
                        label = insumo.nombreInsumo,
                        value = insumo.costoTotal.toFloat(),
                        color = coloresInsumos[index % coloresInsumos.size]
                    )
                },
                plotType = PlotType.Pie
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ──────────────────────────────────────────────
    // Sección 2 — Comparador de campañas
    // Nota: paso preparatorio del Issue #302
    // ──────────────────────────────────────────────

    private val _campaniaA = MutableStateFlow<Campania?>(null)
    val campaniaA: StateFlow<Campania?> = _campaniaA.asStateFlow()

    private val _campaniaB = MutableStateFlow<Campania?>(null)
    val campaniaB: StateFlow<Campania?> = _campaniaB.asStateFlow()

    /** Selecciona la Campaña A del comparador. */
    fun seleccionarCampaniaA(campania: Campania) { _campaniaA.value = campania }

    /** Selecciona la Campaña B del comparador. */
    fun seleccionarCampaniaB(campania: Campania) { _campaniaB.value = campania }

    @OptIn(ExperimentalCoroutinesApi::class)
    val insumosA: StateFlow<List<CampaniaInsumo>> = _campaniaA
        .flatMapLatest { campania ->
            if (campania != null) obtenerInsumosVinculadosUseCase(campania.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val insumosB: StateFlow<List<CampaniaInsumo>> = _campaniaB
        .flatMapLatest { campania ->
            if (campania != null) obtenerInsumosVinculadosUseCase(campania.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ──────────────────────────────────────────────
    // Exportación (por campaña seleccionada en Sección 1)
    // ──────────────────────────────────────────────

    fun exportarReporteCsv(uri: Uri, context: Context) {
        val campania = campaniaIndividual.value
        if (campania == null) {
            _exportStatus.value = "Seleccione una campaña para exportar"
            return
        }
        viewModelScope.launch {
            val data = exportableData.value
            val success = ReportExporter.exportToCsv(uri, context, data, campania.nombre)
            _exportStatus.value = if (success) "Reporte CSV exportado exitosamente" else "Error al exportar reporte CSV"
        }
    }

    fun exportarReportePdf(uri: Uri, context: Context) {
        val campania = campaniaIndividual.value
        if (campania == null) {
            _exportStatus.value = "Seleccione una campaña para exportar"
            return
        }
        viewModelScope.launch {
            val data = exportableData.value
            val success = ReportExporter.exportToPdf(uri, context, data, campania.nombre)
            _exportStatus.value = if (success) "Reporte PDF exportado exitosamente" else "Error al exportar reporte PDF"
        }
    }
}
