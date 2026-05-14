package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar una tarea existente.
 */
class EliminarTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(tarea: Tarea) {
        tareaRepository.deleteTarea(tarea)
    }
}
