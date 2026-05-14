package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.TareaRepository
import javax.inject.Inject

/**
 * Caso de uso para confirmar (marcar como completada) o desconfirmar una tarea.
 * Actualiza el estado booleano de confirmación de la tarea.
 */
class ConfirmarTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(tareaId: Int, completada: Boolean) {
        tareaRepository.completeTarea(tareaId, completada)
    }
}
