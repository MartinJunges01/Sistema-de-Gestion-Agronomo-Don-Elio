package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar una campaña existente.
 */
class EliminarCampaniaUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    suspend operator fun invoke(campania: Campania) {
        campaniaRepository.deleteCampania(campania)
    }
}
