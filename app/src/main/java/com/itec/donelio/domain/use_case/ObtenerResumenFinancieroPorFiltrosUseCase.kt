package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import com.itec.donelio.domain.repository.CosechaRepository
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObtenerResumenFinancieroPorFiltrosUseCase @Inject constructor(
    private val campaniaInsumoRepository: CampaniaInsumoRepository,
    private val cosechaRepository: CosechaRepository,
    private val cosechaNoAlmacenadaRepository: CosechaNoAlmacenadaRepository
) {
    operator fun invoke(
        filtroCampanias: List<Int>,
        filtroFechas: Pair<Long, Long>?
    ): Flow<ResumenRendimiento?> {
        return combine(
            campaniaInsumoRepository.getAllInsumosUtilizados(),
            cosechaRepository.getAllCosechas(),
            cosechaNoAlmacenadaRepository.getAllNoAlmacenadas()
        ) { todosInsumos, todasCosechas, todasNoAlmacenadas ->

            // 1. Insumos (Capital Invertido)
            // CampaniaInsumo no tiene fecha, filtramos solo por campaña
            val insumosFiltrados = if (filtroCampanias.isNotEmpty()) {
                todosInsumos.filter { it.idCampania in filtroCampanias }
            } else {
                todosInsumos
            }
            val capitalInvertido = insumosFiltrados.sumOf { it.cantidad * it.precio }

            // 2. Cosechas
            val cosechasFiltradas = todasCosechas.filter { cosecha ->
                val pasaCampania = if (filtroCampanias.isNotEmpty()) cosecha.idCampania in filtroCampanias else true
                val pasaFecha = if (filtroFechas != null) {
                    cosecha.fecha in filtroFechas.first..filtroFechas.second
                } else true
                pasaCampania && pasaFecha
            }
            val totalCosechado = cosechasFiltradas.sumOf { it.cantidad }
            val costoPorTn = if (totalCosechado > 0) capitalInvertido / totalCosechado else 0.0

            // 3. Ventas (Ingresos Brutos)
            val idsCosechasFiltradas = cosechasFiltradas.map { it.id }.toSet()
            val ventasFiltradas = todasNoAlmacenadas.filter { noAlmacenada ->
                noAlmacenada.precio > 0.0 && noAlmacenada.idCosecha in idsCosechasFiltradas
            }
            val ingresosBrutos = ventasFiltradas.sumOf { it.precio }

            // 4. Balance
            val balance = ingresosBrutos - capitalInvertido

            ResumenRendimiento(
                capitalInvertido = capitalInvertido,
                ingresosBrutos = ingresosBrutos,
                balance = balance,
                totalCosechado = totalCosechado,
                costoPorTonelada = costoPorTn
            )
        }
    }
}
