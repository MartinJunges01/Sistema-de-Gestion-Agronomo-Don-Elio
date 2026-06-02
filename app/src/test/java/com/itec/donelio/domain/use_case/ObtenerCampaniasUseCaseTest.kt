package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObtenerCampaniasUseCaseTest {

    private lateinit var campaniaRepository: CampaniaRepository
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase

    @Before
    fun setUp() {
        campaniaRepository = mockk()
        obtenerCampaniasUseCase = ObtenerCampaniasUseCase(campaniaRepository)
    }

    @Test
    fun `invoke calls getCampanias and returns flow`() = runTest {
        val campanias = listOf(
            Campania(id = 1, nombre = "Trigo", cultivo = "Trigo", fechaInicio = 1L, estaActiva = true),
            Campania(id = 2, nombre = "Soja", cultivo = "Soja", fechaInicio = 2L, estaActiva = false)
        )
        every { campaniaRepository.getCampanias() } returns flowOf(campanias)

        obtenerCampaniasUseCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Trigo", result[0].nombre)
            assertEquals("Soja", result[1].nombre)
            awaitComplete()
        }

        verify(exactly = 1) { campaniaRepository.getCampanias() }
    }
}
