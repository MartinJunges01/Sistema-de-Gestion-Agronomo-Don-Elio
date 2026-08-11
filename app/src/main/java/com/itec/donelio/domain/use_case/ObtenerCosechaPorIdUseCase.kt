package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import javax.inject.Inject

/**
 * UseCase encargado de obtener una cosecha por su ID.
 */
class ObtenerCosechaPorIdUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository
) {
    /**
     * Obtiene una cosecha por su ID.
     * @param id El ID de la cosecha.
     * @return La cosecha si existe, o null en caso contrario.
     */
    suspend operator fun invoke(id: Int): Cosecha? {
        return cosechaRepository.getCosechaById(id)
    }
}
