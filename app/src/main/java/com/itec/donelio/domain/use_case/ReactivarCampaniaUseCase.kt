package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.CampaniaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Caso de uso para reactivar una campaña archivada.
 * La campaña volverá a aparecer en la lista de activas y desaparecerá del historial.
 */
class ReactivarCampaniaUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    operator fun invoke(campania: Campania): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val campaniaReactivada = campania.copy(estaActiva = true)
            campaniaRepository.updateCampania(campaniaReactivada)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error desconocido", e))
        }
    }.flowOn(Dispatchers.IO)
}
