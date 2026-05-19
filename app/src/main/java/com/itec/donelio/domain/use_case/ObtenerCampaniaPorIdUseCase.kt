package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerCampaniaPorIdUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    operator fun invoke(id: Int): Flow<Campania?> {
        return campaniaRepository.getCampanias().map { lista ->
            lista.find { it.id == id }
        }
    }
}
