package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObtenerEvolucionCultivoUseCaseTest {

    private lateinit var campaniaRepository: CampaniaRepository
    private lateinit var cosechaRepository: CosechaRepository
    private lateinit var useCase: ObtenerEvolucionCultivoUseCase

    @Before
    fun setUp() {
        campaniaRepository = mockk()
        cosechaRepository = mockk()
        useCase = ObtenerEvolucionCultivoUseCase(campaniaRepository, cosechaRepository)
    }

    @Test
    fun `invoke con campanias finalizadas y cosechas retorna puntos correctos`() = runTest {
        val campanias = listOf(
            Campania(id = 1, nombre = "Soja 1", hectareas = 100.0, fechaInicio = 1000, estaActiva = false, cultivoId = 1, cultivoNombre = ""),
            Campania(id = 2, nombre = "Soja 2", hectareas = 200.0, fechaInicio = 2000, estaActiva = false, cultivoId = 1, cultivoNombre = "")
        )
        val cosechas = listOf(
            Cosecha(id = 1, idCampania = 1, cantidad = 300.0, fecha = 0, almacenado = false, almacen = ""),
            Cosecha(id = 2, idCampania = 2, cantidad = 400.0, fecha = 0, almacenado = false, almacen = "")
        )
        every { campaniaRepository.getCampanias() } returns flowOf(campanias)
        every { cosechaRepository.getAllCosechas() } returns flowOf(cosechas)

        useCase(1).test {
            val resultado = awaitItem()
            assertEquals(2, resultado.size)
            assertEquals(3.0, resultado[0].rendimientoTnHa, 0.0)
            assertEquals("Soja 1", resultado[0].campaniaNombre)
            assertEquals(2.0, resultado[1].rendimientoTnHa, 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke con campania finalizada con hectareas cero no crashea y retorna 0`() = runTest {
        val campanias = listOf(
            Campania(id = 1, nombre = "Soja 1", hectareas = 0.0, fechaInicio = 1000, estaActiva = false, cultivoId = 1, cultivoNombre = "")
        )
        val cosechas = listOf(
            Cosecha(id = 1, idCampania = 1, cantidad = 300.0, fecha = 0, almacenado = false, almacen = "")
        )
        every { campaniaRepository.getCampanias() } returns flowOf(campanias)
        every { cosechaRepository.getAllCosechas() } returns flowOf(cosechas)

        useCase(1).test {
            val resultado = awaitItem()
            assertEquals(1, resultado.size)
            assertEquals(0.0, resultado[0].rendimientoTnHa, 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke sin campanias para el cultivo retorna lista vacia`() = runTest {
        val campanias = listOf(
            Campania(id = 1, nombre = "Maiz", hectareas = 100.0, fechaInicio = 1000, estaActiva = false, cultivoId = 2, cultivoNombre = "")
        )
        every { campaniaRepository.getCampanias() } returns flowOf(campanias)
        every { cosechaRepository.getAllCosechas() } returns flowOf(emptyList())

        useCase(1).test {
            val resultado = awaitItem()
            assertEquals(0, resultado.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke excluye campanias activas`() = runTest {
        val campanias = listOf(
            Campania(id = 1, nombre = "Soja Activa", hectareas = 100.0, fechaInicio = 1000, estaActiva = true, cultivoId = 1, cultivoNombre = "")
        )
        every { campaniaRepository.getCampanias() } returns flowOf(campanias)
        every { cosechaRepository.getAllCosechas() } returns flowOf(emptyList())

        useCase(1).test {
            val resultado = awaitItem()
            assertEquals(0, resultado.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
