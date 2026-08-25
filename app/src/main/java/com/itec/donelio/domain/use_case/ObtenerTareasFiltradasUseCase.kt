package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class ObtenerTareasFiltradasUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(
        campaniaId: Int?,
        rangoFechas: Pair<Long, Long>?
    ): Flow<List<Tarea>> {
        val baseFlow = if (campaniaId != null && campaniaId != -1) {
            tareaRepository.getTareasByCampania(campaniaId)
        } else {
            tareaRepository.getAllTareas()
        }

        return baseFlow.map { tareas ->
            if (rangoFechas == null) {
                // Sin filtro de fechas: Mostrar solo pendientes y no vencidas (fecha >= hoy)
                val hoy = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                tareas.filter { !it.confirmar && it.fecha >= hoy }
            } else {
                // Con filtro de fechas: Mostrar todas (incluyendo completadas/vencidas) en el rango
                val (desde, hasta) = rangoFechas
                
                // Asegurar que "hasta" cubra todo el día (23:59:59)
                val hastaFinDelDia = Calendar.getInstance().apply {
                    timeInMillis = hasta
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                
                tareas.filter { it.fecha in desde..hastaFinDelDia }
            }
        }
    }
}
