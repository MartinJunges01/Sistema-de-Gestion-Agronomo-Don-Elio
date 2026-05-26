package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerTareasPorCampaniaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(campaniaId: Int): Flow<List<Tarea>> {
        return tareaRepository.getTareasByCampania(campaniaId)
    }
}
