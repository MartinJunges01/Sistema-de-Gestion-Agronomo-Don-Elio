package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.repository.CultivoRepository
import javax.inject.Inject

class CrearCultivoUseCase @Inject constructor(
    private val repository: CultivoRepository
) {
    suspend operator fun invoke(nombre: String): Long {
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del cultivo no puede estar vacío")
        }
        val cultivo = Cultivo(
            id = 0,
            nombre = nombre.trim(),
            activo = true
        )
        return repository.insertCultivo(cultivo)
    }
}
