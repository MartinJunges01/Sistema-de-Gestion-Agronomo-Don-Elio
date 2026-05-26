package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerCosechasNoAlmacenadasUseCase @Inject constructor(
    private val noAlmacenadaRepository: CosechaNoAlmacenadaRepository
) {
    operator fun invoke(campaniaId: Int): Flow<Map<Int, CosechaNoAlmacenada>> {
        return noAlmacenadaRepository.getNoAlmacenadasPorCampania(campaniaId)
            .map { lista -> lista.associateBy { it.idCosecha } }
    }
}
