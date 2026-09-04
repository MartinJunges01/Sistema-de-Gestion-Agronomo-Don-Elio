package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerTodosLosInsumosUtilizadosUseCase @Inject constructor(
    private val campaniaInsumoRepository: CampaniaInsumoRepository
) {
    operator fun invoke(): Flow<List<CampaniaInsumo>> {
        return campaniaInsumoRepository.getAllInsumosUtilizados()
    }
}
