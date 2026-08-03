package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el listado de campañas inactivas (historial).
 */
class ObtenerCampaniasInactivasUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository
) {
    operator fun invoke(): Flow<List<Campania>> {
        return campaniaRepository.getCampaniasInactivas()
    }
}
