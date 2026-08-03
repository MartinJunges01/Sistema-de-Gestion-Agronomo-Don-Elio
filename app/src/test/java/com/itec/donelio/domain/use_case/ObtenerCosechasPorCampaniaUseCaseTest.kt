package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObtenerCosechasPorCampaniaUseCaseTest {

    private lateinit var cosechaRepository: CosechaRepository
    private lateinit var obtenerCosechasPorCampaniaUseCase: ObtenerCosechasPorCampaniaUseCase

    @Before
    fun setUp() {
        cosechaRepository = mockk()
        obtenerCosechasPorCampaniaUseCase = ObtenerCosechasPorCampaniaUseCase(cosechaRepository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        // Given
        val campaniaId = 1
        val cosechas = listOf(
            Cosecha(1, 100.0, 1680000000000L, "Silo A", campaniaId),
            Cosecha(2, 50.0, 1680000000000L, "Silo B", campaniaId)
        )
        every { cosechaRepository.getCosechasByCampania(campaniaId) } returns flowOf(cosechas)

        // When
        obtenerCosechasPorCampaniaUseCase(campaniaId).test {
            // Then
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Silo A", result[0].almacen)
            assertEquals("Silo B", result[1].almacen)
            
            awaitComplete()
        }

        verify(exactly = 1) { cosechaRepository.getCosechasByCampania(campaniaId) }
    }
}
