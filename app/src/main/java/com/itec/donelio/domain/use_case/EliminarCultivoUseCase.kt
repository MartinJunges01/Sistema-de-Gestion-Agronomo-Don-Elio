package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CultivoRepository
import javax.inject.Inject

class EliminarCultivoUseCase @Inject constructor(
    private val repository: CultivoRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteCultivo(id)
    }
}
