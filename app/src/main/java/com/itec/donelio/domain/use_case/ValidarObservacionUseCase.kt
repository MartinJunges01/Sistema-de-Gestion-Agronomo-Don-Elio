package com.itec.donelio.domain.use_case

import javax.inject.Inject

/**
 * Caso de uso para validar que una observación tenga texto o una foto.
 */
class ValidarObservacionUseCase @Inject constructor() {
    operator fun invoke(texto: String, imagenUri: String?): Boolean {
        return texto.isNotBlank() || imagenUri != null
    }
}
