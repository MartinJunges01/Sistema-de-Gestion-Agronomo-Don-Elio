package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import javax.inject.Inject

/**
 * UseCase encargado de actualizar una cosecha existente.
 */
class EditarCosechaUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository
) {
    /**
     * Actualiza una cosecha existente en el repositorio.
     * @param cosecha La cosecha con los datos actualizados.
     */
    suspend operator fun invoke(cosecha: Cosecha) {
        cosechaRepository.updateCosecha(cosecha)
    }
}
