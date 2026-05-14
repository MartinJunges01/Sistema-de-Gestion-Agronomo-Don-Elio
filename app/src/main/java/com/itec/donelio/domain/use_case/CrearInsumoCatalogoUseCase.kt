package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import javax.inject.Inject

/**
 * Caso de uso para crear un nuevo insumo en el catálogo global.
 * Valida que el nombre no esté vacío antes de persistir.
 */
class CrearInsumoCatalogoUseCase @Inject constructor(
    private val insumoRepository: InsumoRepository
) {
    suspend operator fun invoke(
        nombre: String,
        categoria: String,
        unidad: String
    ) {
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del insumo no puede estar vacío")
        }
        val insumo = Insumo(
            id = 0,
            nombre = nombre.trim(),
            categoria = categoria.trim(),
            unidad = unidad.trim()
        )
        insumoRepository.insertInsumo(insumo)
    }
}
