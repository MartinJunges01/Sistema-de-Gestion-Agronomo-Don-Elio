package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva tarea asociada a una campaña.
 * Valida que el nombre no esté vacío antes de persistir.
 */
class CrearTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(
        nombre: String,
        fecha: Long,
        hora: String,
        notificar: Boolean,
        idCampania: Int
    ) {
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre de la tarea no puede estar vacío")
        }
        val tarea = Tarea(
            id = 0,
            nombre = nombre.trim(),
            fecha = fecha,
            hora = hora,
            notificar = notificar,
            confirmar = false,
            idCampania = idCampania
        )
        tareaRepository.insertTarea(tarea)
    }
}
