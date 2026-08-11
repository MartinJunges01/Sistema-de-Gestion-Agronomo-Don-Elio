package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import javax.inject.Inject

/**
 * UseCase encargado de eliminar una cosecha existente.
 */
class EliminarCosechaUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository
) {
    /**
     * Elimina una cosecha y sus registros asociados (CASCADE via FK).
     * @param cosecha La cosecha a eliminar.
     */
    suspend operator fun invoke(cosecha: Cosecha) {
        cosechaRepository.deleteCosecha(cosecha)
    }
}
