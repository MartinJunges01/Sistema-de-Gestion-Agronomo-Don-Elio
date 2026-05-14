package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import javax.inject.Inject

/**
 * Caso de uso para asignar un insumo del catálogo a una campaña.
 * Valida que la cantidad sea positiva antes de persistir.
 */
class AsignarInsumoACampaniaUseCase @Inject constructor(
    private val campaniaInsumoRepository: CampaniaInsumoRepository
) {
    suspend operator fun invoke(
        idCampania: Int,
        idInsumo: Int,
        cantidad: Double,
        precio: Double
    ) {
        if (cantidad <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a cero")
        }
        val asignacion = CampaniaInsumo(
            id = 0,
            idCampania = idCampania,
            idInsumo = idInsumo,
            cantidad = cantidad,
            precio = precio
        )
        campaniaInsumoRepository.asignarInsumo(asignacion)
    }
}
