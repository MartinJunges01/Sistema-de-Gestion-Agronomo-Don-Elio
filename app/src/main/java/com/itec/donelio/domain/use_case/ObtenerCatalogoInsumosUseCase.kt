package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el listado completo del catálogo de insumos.
 */
class ObtenerCatalogoInsumosUseCase @Inject constructor(
    private val insumoRepository: InsumoRepository
) {
    operator fun invoke(): Flow<List<Insumo>> {
        return insumoRepository.getAllInsumos()
    }
}
