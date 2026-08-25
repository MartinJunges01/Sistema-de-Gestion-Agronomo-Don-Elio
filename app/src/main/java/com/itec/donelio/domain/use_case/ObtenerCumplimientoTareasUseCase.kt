package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class ObtenerCumplimientoTareasUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(): Flow<CumplimientoTareas?> {
        val (inicioSemana, finSemana) = obtenerLimitesSemanaActual()

        return tareaRepository.getAllTareas().map { tareas ->
            val tareasDeLaSemana = tareas.filter { it.fecha in inicioSemana..finSemana }
            
            if (tareasDeLaSemana.isEmpty()) {
                return@map null
            }

            val completadas = tareasDeLaSemana.count { it.confirmar }
            val total = tareasDeLaSemana.size
            val porcentaje = (completadas.toFloat() / total.toFloat()) * 100f

            CumplimientoTareas(
                completadas = completadas,
                total = total,
                porcentaje = porcentaje
            )
        }
    }

    private fun obtenerLimitesSemanaActual(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        
        // Ajustar al lunes
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val inicio = cal.timeInMillis

        // Ajustar al domingo
        cal.add(Calendar.DAY_OF_YEAR, 6)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val fin = cal.timeInMillis

        return Pair(inicio, fin)
    }
}
