package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.PuntoCultivo
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerEvolucionCultivoUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository,
    private val cosechaRepository: CosechaRepository
) {
    operator fun invoke(cultivoId: Int): Flow<List<PuntoCultivo>> {
        return campaniaRepository.getCampanias().map { campanias ->
            // Filtramos las campañas que pertenezcan a este cultivo (solo finalizadas o todas? El historial es sobre finalizadas o en general. Asumimos finalizadas)
            campanias.filter { it.cultivoId == cultivoId && !it.estaActiva }.sortedBy { it.fechaInicio }
        }.combine(cosechaRepository.getAllCosechas()) { campaniasFiltradas, todasCosechas ->
            campaniasFiltradas.map { campania ->
                val cosechasCampania = todasCosechas.filter { it.idCampania == campania.id }
                val totalCosechado = cosechasCampania.sumOf { it.cantidad }
                val rendimiento = if (campania.hectareas > 0) totalCosechado / campania.hectareas else 0.0
                
                PuntoCultivo(
                    campaniaNombre = campania.nombre,
                    rendimientoTnHa = rendimiento,
                    fecha = campania.fechaInicio
                )
            }
        }
    }
}
