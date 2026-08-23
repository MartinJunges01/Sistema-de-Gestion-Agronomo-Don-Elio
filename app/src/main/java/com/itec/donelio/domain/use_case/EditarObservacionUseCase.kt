package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.ObservacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EditarObservacionUseCase @Inject constructor(
    private val observacionRepository: ObservacionRepository
) {
    operator fun invoke(observacion: Observacion): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val obsLimpia = observacion.copy(texto = observacion.texto.trim())
            if (obsLimpia.texto.isBlank() && obsLimpia.imagenUri == null) {
                emit(Resource.Error("La observación debe tener texto o una foto"))
                return@flow
            }
            
            observacionRepository.updateObservacion(obsLimpia)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al editar observación: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)
}
