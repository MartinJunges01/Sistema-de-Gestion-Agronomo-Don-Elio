package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import javax.inject.Inject

class EliminarInsumoCatalogoUseCase @Inject constructor(
    private val insumoRepository: InsumoRepository
) {
    suspend operator fun invoke(insumo: Insumo) {
        insumoRepository.deleteInsumo(insumo)
    }
}
