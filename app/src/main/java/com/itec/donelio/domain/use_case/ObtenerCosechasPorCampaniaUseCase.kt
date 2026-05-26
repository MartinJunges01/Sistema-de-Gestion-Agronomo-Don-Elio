package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerCosechasPorCampaniaUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository
) {
    operator fun invoke(campaniaId: Int): Flow<List<Cosecha>> {
        return cosechaRepository.getCosechasByCampania(campaniaId)
    }
}
