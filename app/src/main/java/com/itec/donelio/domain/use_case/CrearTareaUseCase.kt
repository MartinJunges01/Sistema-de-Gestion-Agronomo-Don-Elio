package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.core.alarm.TaskReminderScheduler
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva tarea asociada a una campaña.
 * Valida que el nombre no esté vacío antes de persistir.
 */
class CrearTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    operator fun invoke(
        nombre: String,
        fecha: Long,
        hora: String,
        notificar: Boolean,
        idCampania: Int
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            if (nombre.isBlank()) {
                throw IllegalArgumentException("El nombre de la tarea no puede estar vacío")
            }
            val tarea = Tarea(
                id = 0,
                nombre = nombre.trim(),
                fecha = fecha,
                hora = hora,
                notificar = notificar,
                confirmar = false,
                idCampania = idCampania
            )
            val newId = tareaRepository.insertTarea(tarea)
            val insertedTarea = tarea.copy(id = newId.toInt())
            if (insertedTarea.notificar) {
                taskReminderScheduler.schedule(insertedTarea)
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error desconocido", e))
        }
    }.flowOn(Dispatchers.IO)
}
