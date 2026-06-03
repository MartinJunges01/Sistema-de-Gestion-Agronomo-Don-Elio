package com.itec.donelio.presentation.viewmodel.reportes

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.itec.donelio.core.utils.ReportExporter
import com.itec.donelio.domain.model.InsumoResumen
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import com.itec.donelio.domain.use_case.ObtenerTodosLosInsumosVinculadosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportesViewModel @Inject constructor(
    obtenerTodosLosInsumosVinculadosUseCase: ObtenerTodosLosInsumosVinculadosUseCase,
    obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase
) : ViewModel() {

    private val coloresInsumos = listOf(
        Color(0xFF15803d),
        Color(0xFF1d4ed8),
        Color(0xFFd97706),
        Color(0xFFb91c1c),
        Color(0xFF6b21a8),
        Color(0xFF0369a1),
        Color(0xFF4d7c0f)
    )

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus

    fun clearExportStatus() {
        _exportStatus.value = null
    }

    val exportableData: StateFlow<List<InsumoResumen>> = combine(
        obtenerTodosLosInsumosVinculadosUseCase(),
        obtenerCatalogoInsumosUseCase()
    ) { vinculados, catalogo ->
        val catalogoMap = catalogo.associateBy { it.id }
        
        vinculados.groupBy { it.idInsumo }
            .map { entry ->
                val nombre = catalogoMap[entry.key]?.nombre ?: "Desconocido"
                val cantidadTotal = entry.value.sumOf { it.cantidad }
                val costoTotal = entry.value.sumOf { it.cantidad * it.precio }
                InsumoResumen(nombre, cantidadTotal, costoTotal)
            }
            .filter { it.costoTotal > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pieChartData: StateFlow<PieChartData?> = exportableData.mapState { insumosResumen ->
        if (insumosResumen.isEmpty()) return@mapState null

        val slices = insumosResumen.mapIndexed { index, insumo ->
            PieChartData.Slice(
                label = insumo.nombreInsumo,
                value = insumo.costoTotal.toFloat(),
                color = coloresInsumos[index % coloresInsumos.size]
            )
        }

        PieChartData(
            slices = slices,
            plotType = PlotType.Pie
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun exportarReporteCsv(uri: Uri, context: Context) {
        viewModelScope.launch {
            val data = exportableData.value
            val success = ReportExporter.exportToCsv(uri, context, data)
            _exportStatus.value = if (success) "Reporte CSV exportado exitosamente" else "Error al exportar reporte CSV"
        }
    }

    fun exportarReportePdf(uri: Uri, context: Context) {
        viewModelScope.launch {
            val data = exportableData.value
            val success = ReportExporter.exportToPdf(uri, context, data)
            _exportStatus.value = if (success) "Reporte PDF exportado exitosamente" else "Error al exportar reporte PDF"
        }
    }
}

private fun <T, R> StateFlow<T>.mapState(transform: (T) -> R): kotlinx.coroutines.flow.Flow<R> =
    kotlinx.coroutines.flow.map { transform(it) }
