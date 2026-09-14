package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import javax.inject.Inject

/**
 * Caso de uso para editar una cosecha que puede tener o no un registro de venta asociado.
 *
 * Al editar:
 * - Si [esAlmacenada] es true: actualiza la cosecha base y elimina el registro de venta si existía.
 * - Si [esAlmacenada] es false: actualiza la cosecha base y actualiza (o crea) el registro de venta.
 *
 * @param cosechaRepository Repositorio de la tabla principal de cosechas.
 * @param cosechaNoAlmacenadaRepository Repositorio de la tabla de ventas/reservas.
 */
class EditarCosechaConVentaUseCase @Inject constructor(
    private val cosechaRepository: CosechaRepository,
    private val cosechaNoAlmacenadaRepository: CosechaNoAlmacenadaRepository
) {
    suspend operator fun invoke(
        cosecha: Cosecha,
        esAlmacenada: Boolean,
        tipo: String,
        precioTotal: Double
    ) {
        // 1. Actualizar el registro base de la cosecha
        cosechaRepository.updateCosecha(cosecha)

        // 2. Buscar si ya existe un registro de venta para esta cosecha
        val detalleExistente = cosechaNoAlmacenadaRepository.getPorCosechaId(cosecha.id)

        if (esAlmacenada) {
            // Si ahora es almacenada, eliminar el registro de venta si existía
            if (detalleExistente != null) {
                cosechaNoAlmacenadaRepository.delete(detalleExistente)
            }
        } else {
            // Si es venta/reserva, actualizar o crear el detalle
            val nuevoDetalle = CosechaNoAlmacenada(
                id = detalleExistente?.id ?: 0,
                tipo = tipo.trim(),
                precio = precioTotal,
                idCosecha = cosecha.id
            )
            if (detalleExistente != null) {
                cosechaNoAlmacenadaRepository.delete(detalleExistente)
            }
            cosechaNoAlmacenadaRepository.insert(nuevoDetalle)
        }
    }
}
