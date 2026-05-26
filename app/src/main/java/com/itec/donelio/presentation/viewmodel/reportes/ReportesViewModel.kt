package com.itec.donelio.presentation.viewmodel.reportes

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import com.itec.donelio.domain.use_case.ObtenerTodosLosInsumosVinculadosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    val pieChartData: StateFlow<PieChartData?> = combine(
        obtenerTodosLosInsumosVinculadosUseCase(),
        obtenerCatalogoInsumosUseCase()
    ) { vinculados, catalogo ->
        val catalogoMap = catalogo.associateBy { it.id }
        
        val gastosPorInsumo = vinculados.groupBy { it.idInsumo }
            .mapValues { entry -> entry.value.sumOf { it.cantidad * it.precio } }
            .filterValues { it > 0 }
        
        if (gastosPorInsumo.isEmpty()) return@combine null

        val slices = gastosPorInsumo.entries.mapIndexed { index, entry ->
            val nombre = catalogoMap[entry.key]?.nombre ?: "Desconocido"
            val total = entry.value.toFloat()
            PieChartData.Slice(
                label = nombre,
                value = total,
                color = coloresInsumos[index % coloresInsumos.size]
            )
        }

        PieChartData(
            slices = slices,
            plotType = PlotType.Pie
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
