package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.ObservacionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EliminarObservacionUseCase @Inject constructor(
    private val observacionRepository: ObservacionRepository
) {
    operator fun invoke(observacion: Observacion): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            observacionRepository.deleteObservacion(observacion)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar observación: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)
}
