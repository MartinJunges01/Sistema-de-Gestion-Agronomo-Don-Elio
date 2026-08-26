package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.repository.CultivoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerCultivosUseCase @Inject constructor(
    private val repository: CultivoRepository
) {
    operator fun invoke(soloActivos: Boolean = true): Flow<List<Cultivo>> {
        return if (soloActivos) {
            repository.getCultivosActivos()
        } else {
            repository.getTodosLosCultivos()
        }
    }
}
