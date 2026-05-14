package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva campaña.
 * Valida que el nombre no esté vacío antes de persistir.
 */
class CrearCampaniaUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    suspend operator fun invoke(nombre: String, fechaInicio: Long) {
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre de la campaña no puede estar vacío")
        }
        val campania = Campania(
            id = 0,
            nombre = nombre.trim(),
            fechaInicio = fechaInicio,
            estaActiva = true
        )
        campaniaRepository.insertCampania(campania)
    }
}
