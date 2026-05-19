package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.repository.ObservacionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerObservacionesPorCampaniaUseCase @Inject constructor(
    private val observacionRepository: ObservacionRepository
) {
    operator fun invoke(campaniaId: Int): Flow<List<Observacion>> {
        return observacionRepository.getObservacionesPorCampania(campaniaId)
    }
}
