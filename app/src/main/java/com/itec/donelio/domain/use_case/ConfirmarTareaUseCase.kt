package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Resource
import com.itec.donelio.core.alarm.TaskReminderScheduler
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Caso de uso para confirmar (marcar como completada) o desconfirmar una tarea.
 * Actualiza el estado booleano de confirmación de la tarea.
 */
class ConfirmarTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    operator fun invoke(tareaId: Int, completada: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            tareaRepository.completeTarea(tareaId, completada)
            if (completada) {
                taskReminderScheduler.cancel(tareaId)
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error desconocido", e))
        }
    }.flowOn(Dispatchers.IO)
}
