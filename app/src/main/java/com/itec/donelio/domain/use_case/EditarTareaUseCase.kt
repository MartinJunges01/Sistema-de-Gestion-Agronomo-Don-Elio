package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import javax.inject.Inject

/**
 * Caso de uso para editar una tarea existente.
 * Valida que el nombre no esté vacío antes de actualizar.
 */
class EditarTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(tarea: Tarea) {
        if (tarea.nombre.isBlank()) {
            throw IllegalArgumentException("El nombre de la tarea no puede estar vacío")
        }
        tareaRepository.updateTarea(tarea)
    }
}
