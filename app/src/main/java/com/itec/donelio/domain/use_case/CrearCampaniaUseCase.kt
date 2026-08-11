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
 * Caso de uso para crear una nueva campaña.
 * Valida que el nombre no esté vacío antes de persistir.
 */
class CrearCampaniaUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    operator fun invoke(nombre: String, cultivo: String, fechaInicio: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            if (nombre.isBlank()) {
                throw IllegalArgumentException("El nombre de la campaña no puede estar vacío")
            }

            val campania = Campania(
                id = 0,
                nombre = nombre.trim(),
                fechaInicio = fechaInicio,
                estaActiva = true,
                cultivo = cultivo.trim()
            )
            campaniaRepository.insertCampania(campania)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error desconocido", e))
        }
    }.flowOn(Dispatchers.IO)
}
