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
 * Caso de uso para editar una campaña existente.
 * Valida que el nombre no esté vacío antes de actualizar.
 */
class EditarCampaniaUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    operator fun invoke(campania: Campania): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            if (campania.nombre.isBlank()) {
                throw IllegalArgumentException("El nombre de la campaña no puede estar vacío")
            }
            campaniaRepository.updateCampania(campania)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error desconocido", e))
        }
    }.flowOn(Dispatchers.IO)
}
