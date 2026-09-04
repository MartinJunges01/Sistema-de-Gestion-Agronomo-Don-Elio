package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class ObtenerResumenRendimientoUseCase @Inject constructor(
    private val campaniaRepository: CampaniaRepository,
    private val campaniaInsumoRepository: CampaniaInsumoRepository,
    private val cosechaRepository: CosechaRepository,
    private val cosechaNoAlmacenadaRepository: CosechaNoAlmacenadaRepository
) {
    operator fun invoke(): Flow<ResumenRendimiento?> {
        return combine(
            campaniaRepository.getCampaniasActivas(),
            campaniaInsumoRepository.getAllInsumosUtilizados(),
            cosechaRepository.getAllCosechas(),
            cosechaNoAlmacenadaRepository.getAllNoAlmacenadas()
        ) { campaniasActivas, todosInsumos, todasCosechas, todasNoAlmacenadas ->
            if (campaniasActivas.isEmpty()) return@combine null

            val idsActivas = campaniasActivas.map { it.id }.toSet()

            // Fechas del mes actual
            val cal = Calendar.getInstance()
            val anioActual = cal.get(Calendar.YEAR)
            val mesActual = cal.get(Calendar.MONTH)

            // Filtrar y sumar insumos de campañas activas (CampaniaInsumo no tiene fecha, tomamos todo el costo de la campaña activa)
            // Se asume que la inversión de una campaña activa corresponde al ciclo actual.
            val insumosActivos = todosInsumos.filter { it.idCampania in idsActivas }
            val capitalInvertido = insumosActivos.sumOf { it.cantidad * it.precio }

            // Filtrar y sumar cosechas de campañas activas, PERO solo las de este mes.
            val cosechasDelMes = todasCosechas.filter { cosecha ->
                if (cosecha.idCampania !in idsActivas) return@filter false
                
                cal.timeInMillis = cosecha.fecha
                val esMesActual = cal.get(Calendar.YEAR) == anioActual && cal.get(Calendar.MONTH) == mesActual
                esMesActual
            }
            val totalCosechado = cosechasDelMes.sumOf { it.cantidad }

            val costoPorTn = if (totalCosechado > 0) {
                capitalInvertido / totalCosechado
            } else {
                0.0
            }
            
            // Filtrar y sumar ingresos por ventas de campañas activas en este mes.
            val idsCosechasDelMes = cosechasDelMes.map { it.id }.toSet()
            val ventasDelMes = todasNoAlmacenadas.filter { noAlmacenada ->
                noAlmacenada.tipo.equals("venta", ignoreCase = true) && noAlmacenada.idCosecha in idsCosechasDelMes
            }
            val ingresosBrutos = ventasDelMes.sumOf { venta ->
                val cosecha = cosechasDelMes.find { it.id == venta.idCosecha }
                val cantidad = cosecha?.cantidad ?: 0.0
                cantidad * venta.precio
            }
            val balance = ingresosBrutos - capitalInvertido

            ResumenRendimiento(
                capitalInvertido = capitalInvertido,
                ingresosBrutos = ingresosBrutos,
                balance = balance,
                totalCosechado = totalCosechado,
                costoPorTonelada = costoPorTn
            )
        }
    }
}
