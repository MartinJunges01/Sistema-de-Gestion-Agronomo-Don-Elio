package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import javax.inject.Inject

/**
 * Caso de uso para editar una campaña existente.
 * Valida que el nombre no esté vacío antes de actualizar.
 */
class EditarCampaniaUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    suspend operator fun invoke(campania: Campania) {
        if (campania.nombre.isBlank()) {
            throw IllegalArgumentException("El nombre de la campaña no puede estar vacío")
        }
        campaniaRepository.updateCampania(campania)
    }
}
