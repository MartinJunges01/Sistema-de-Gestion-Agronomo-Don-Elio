package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.CampaniaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FinalizarCampaniaUseCaseTest {

    private lateinit var campaniaRepository: CampaniaRepository
    private lateinit var finalizarCampaniaUseCase: FinalizarCampaniaUseCase

    @Before
    fun setUp() {
        campaniaRepository = mockk()
        finalizarCampaniaUseCase = FinalizarCampaniaUseCase(campaniaRepository)
    }

    @Test
    fun `dadoCampaniaActiva_cuandoSeFinaliza_entoncesEstaActivaPasaAFalse`() = runTest {
        // Given
        val campania = Campania(
            id = 1,
            nombre = "Soja 2026",
            cultivo = "Soja",
            fechaInicio = 1000L,
            estaActiva = true
        )
        val campaniaFinalizada = campania.copy(estaActiva = false)
        
        coEvery { campaniaRepository.updateCampania(any()) } returns Unit

        // When
        finalizarCampaniaUseCase(campania).test {
            // Then
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 1) { campaniaRepository.updateCampania(campaniaFinalizada) }
    }
}
