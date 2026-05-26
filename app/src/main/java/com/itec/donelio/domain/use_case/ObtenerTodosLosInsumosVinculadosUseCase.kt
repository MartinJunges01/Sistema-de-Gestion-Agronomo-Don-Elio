package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.InsumoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerTodosLosInsumosVinculadosUseCase @Inject constructor(
    private val insumoRepository: InsumoRepository
) {
    operator fun invoke(): Flow<List<CampaniaInsumo>> {
        return insumoRepository.getAllInsumosVinculados()
    }
}
