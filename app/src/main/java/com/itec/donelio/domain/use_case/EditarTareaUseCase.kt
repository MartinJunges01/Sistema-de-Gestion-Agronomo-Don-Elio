package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Caso de uso para editar una tarea existente.
 * Valida que el nombre no esté vacío antes de actualizar.
 */
class EditarTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(tarea: Tarea): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            if (tarea.nombre.isBlank()) {
                throw IllegalArgumentException("El nombre de la tarea no puede estar vacío")
            }
            tareaRepository.updateTarea(tarea)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error desconocido", e))
        }
    }.flowOn(Dispatchers.IO)
}
