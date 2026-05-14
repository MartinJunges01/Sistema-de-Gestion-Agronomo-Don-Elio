package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.repository.ObservacionRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar una observación asociada a una campaña.
 * Valida que el texto no esté vacío antes de persistir.
 */
class GuardarObservacionUseCase @Inject constructor(
    private val observacionRepository: ObservacionRepository
) {
    suspend operator fun invoke(
        texto: String,
        imagenUri: String?,
        idCampania: Int
    ) {
        if (texto.isBlank()) {
            throw IllegalArgumentException("El texto de la observación no puede estar vacío")
        }
        val observacion = Observacion(
            id = 0,
            texto = texto.trim(),
            imagenUri = imagenUri,
            idCampania = idCampania
        )
        observacionRepository.insertObservacion(observacion)
    }
}
