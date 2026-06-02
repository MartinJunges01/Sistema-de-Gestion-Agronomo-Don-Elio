package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.ObservacionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GuardarObservacionUseCaseTest {

    private lateinit var observacionRepository: ObservacionRepository
    private lateinit var guardarObservacionUseCase: GuardarObservacionUseCase

    @Before
    fun setUp() {
        observacionRepository = mockk()
        guardarObservacionUseCase = GuardarObservacionUseCase(observacionRepository)
    }

    @Test
    fun `invoke with valid data calls insertObservacion`() = runTest {
        // Given
        val texto = "Nota de prueba"
        val imagenUri = "content://images/1"
        val idCampania = 1

        coEvery { observacionRepository.insertObservacion(any()) } returns Unit

        // When
        guardarObservacionUseCase(texto, imagenUri, idCampania)

        // Then
        coVerify(exactly = 1) { 
            observacionRepository.insertObservacion(withArg {
                assertEquals(texto, it.texto)
                assertEquals(imagenUri, it.imagenUri)
                assertEquals(idCampania, it.idCampania)
            }) 
        }
    }

    @Test
    fun `invoke with blank texto throws exception`() = runTest {
        // Given
        val texto = "   "
        val imagenUri = "content://images/1"
        val idCampania = 1

        // When / Then
        try {
            guardarObservacionUseCase(texto, imagenUri, idCampania)
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("El texto de la observación no puede estar vacío", e.message)
        }

        coVerify(exactly = 0) { observacionRepository.insertObservacion(any()) }
    }
}
