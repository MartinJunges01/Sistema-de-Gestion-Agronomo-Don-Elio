package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import javax.inject.Inject

class ObtenerInsumoPorIdUseCase @Inject constructor(
    private val repository: InsumoRepository
) {
    suspend operator fun invoke(id: Int): Insumo? = repository.getInsumoById(id)
}