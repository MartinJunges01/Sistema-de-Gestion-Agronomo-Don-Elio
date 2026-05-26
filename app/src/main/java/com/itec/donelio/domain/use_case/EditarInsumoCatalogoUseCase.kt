package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import javax.inject.Inject

/**
 * Caso de uso para editar un insumo existente en el catálogo global.
 * Valida que el nombre no esté vacío antes de actualizar.
 */
class EditarInsumoCatalogoUseCase @Inject constructor(
    private val insumoRepository: InsumoRepository
) {
    suspend operator fun invoke(insumo: Insumo) {
        if (insumo.nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del insumo no puede estar vacío")
        }
        insumoRepository.updateInsumo(insumo)
    }
}
