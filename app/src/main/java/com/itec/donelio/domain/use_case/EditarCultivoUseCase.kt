package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.repository.CultivoRepository
import javax.inject.Inject

class EditarCultivoUseCase @Inject constructor(
    private val repository: CultivoRepository
) {
    suspend operator fun invoke(cultivo: Cultivo) {
        if (cultivo.nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del cultivo no puede estar vacío")
        }
        repository.updateCultivo(cultivo.copy(nombre = cultivo.nombre.trim()))
    }
}
