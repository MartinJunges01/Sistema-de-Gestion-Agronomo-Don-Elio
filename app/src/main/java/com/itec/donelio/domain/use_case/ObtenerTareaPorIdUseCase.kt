package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import javax.inject.Inject

class ObtenerTareaPorIdUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(id: Int): Tarea? {
        return repository.getTareaById(id)
    }
}
