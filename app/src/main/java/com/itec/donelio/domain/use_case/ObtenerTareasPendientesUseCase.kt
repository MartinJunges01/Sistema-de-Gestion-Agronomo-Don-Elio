package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerTareasPendientesUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(limite: Int = 5): Flow<List<Tarea>> {
        return tareaRepository.getTareasPendientesGlobales(limite)
    }
}
