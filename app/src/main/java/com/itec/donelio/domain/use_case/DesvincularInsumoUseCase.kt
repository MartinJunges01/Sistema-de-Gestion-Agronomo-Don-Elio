package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import javax.inject.Inject

class DesvincularInsumoUseCase @Inject constructor(
    private val repository: CampaniaInsumoRepository
) {
    suspend operator fun invoke(campaniaInsumo: CampaniaInsumo) {
        repository.desvincularInsumo(campaniaInsumo)
    }
}
