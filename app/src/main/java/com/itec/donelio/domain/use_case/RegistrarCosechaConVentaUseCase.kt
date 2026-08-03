package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import javax.inject.Inject

class RegistrarCosechaConVentaUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository,
    private val noAlmacenadaRepository: CosechaNoAlmacenadaRepository
) {
    suspend operator fun invoke(
        cantidad: Double,
        fecha: Long,
        idCampania: Int,
        tipo: String,
        precio: Double
    ) {
        if (cantidad <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a cero")
        }
        if (tipo.isBlank()) {
            throw IllegalArgumentException("El tipo de venta no puede estar vacío")
        }
        val idCosecha = cosechaRepository.insertCosecha(
            Cosecha(0, cantidad, fecha, almacen = "", idCampania = idCampania)
        )
        noAlmacenadaRepository.insert(
            CosechaNoAlmacenada(0, tipo.trim(), precio, idCosecha.toInt())
        )
    }
}
