package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerTareasDelDiaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(idCampania: Int, fecha: Long): Flow<List<Tarea>> {
        return tareaRepository.getTareasByCampaniaAndFecha(idCampania, fecha)
    }
}
