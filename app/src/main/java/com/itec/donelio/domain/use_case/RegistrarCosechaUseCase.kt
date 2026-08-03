package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar una nueva cosecha asociada a una campaña.
 * Valida que la cantidad sea positiva antes de persistir.
 */
class RegistrarCosechaUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository
) {
    suspend operator fun invoke(
        cantidad: Double,
        fecha: Long,
        almacen: String,
        idCampania: Int
    ) {
        if (cantidad <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a cero")
        }
        val cosecha = Cosecha(
            id = 0,
            cantidad = cantidad,
            fecha = fecha,
            almacen = almacen.trim(),
            idCampania = idCampania
        )
        cosechaRepository.insertCosecha(cosecha)
    }
}
