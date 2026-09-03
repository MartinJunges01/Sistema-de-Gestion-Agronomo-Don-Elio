package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class ObtenerResumenRendimientoUseCaseTest {

    private lateinit var campaniaRepository: CampaniaRepository
    private lateinit var campaniaInsumoRepository: CampaniaInsumoRepository
    private lateinit var cosechaRepository: CosechaRepository
    private lateinit var cosechaNoAlmacenadaRepository: CosechaNoAlmacenadaRepository
    private lateinit var useCase: ObtenerResumenRendimientoUseCase

    @Before
    fun setUp() {
        campaniaRepository = mockk()
        campaniaInsumoRepository = mockk()
        cosechaRepository = mockk()
        cosechaNoAlmacenadaRepository = mockk()

        useCase = ObtenerResumenRendimientoUseCase(
            campaniaRepository,
            campaniaInsumoRepository,
            cosechaRepository,
            cosechaNoAlmacenadaRepository
        )
    }

    @Test
    fun "resumen financiero calcula correctamente ingresos y balance"() = runTest {
        val cal = Calendar.getInstance()
        val timestampHoy = cal.timeInMillis

        // 1 campaña activa
        val campanias = listOf(
            Campania(id = 1, nombre = "Camp1", cultivoNombre = "Soja", hectareas = 100.0, fechaInicio = timestampHoy, estaActiva = true, ubicacion = "")
        )

        // Inversión: 2 insumos de 100 y 50 de precio, cantidad 2 y 1. Total = 200 + 50 = 250
        val insumos = listOf(
            CampaniaInsumo(id = 1, idCampania = 1, idInsumo = 1, cantidad = 2.0, precio = 100.0),
            CampaniaInsumo(id = 2, idCampania = 1, idInsumo = 2, cantidad = 1.0, precio = 50.0)
        )

        // Cosecha: 1 cosecha de 10 Tn hoy
        val cosechas = listOf(
            Cosecha(id = 1, idCampania = 1, cantidad = 10.0, humedad = 12.0, observaciones = "", fecha = timestampHoy, almacen = "")
        )

        // Venta: vende la cosecha 1 a precio 300 / Tn. Total = 10 * 300 = 3000
        val ventas = listOf(
            CosechaNoAlmacenada(id = 1, idCosecha = 1, tipo = "Venta", precio = 300.0)
        )

        every { campaniaRepository.getCampaniasActivas() } returns flowOf(campanias)
        every { campaniaInsumoRepository.getAllInsumosUtilizados() } returns flowOf(insumos)
        every { cosechaRepository.getAllCosechas() } returns flowOf(cosechas)
        every { cosechaNoAlmacenadaRepository.getAllNoAlmacenadas() } returns flowOf(ventas)

        val resultado = useCase().first()

        assertEquals(250.0, resultado?.capitalInvertido)
        assertEquals(3000.0, resultado?.ingresosBrutos)
        assertEquals(2750.0, resultado?.balance)
    }
}
