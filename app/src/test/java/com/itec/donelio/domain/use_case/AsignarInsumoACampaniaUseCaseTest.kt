package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class AsignarInsumoACampaniaUseCaseTest {

    private lateinit var campaniaInsumoRepository: CampaniaInsumoRepository
    private lateinit var asignarInsumoACampaniaUseCase: AsignarInsumoACampaniaUseCase

    @Before
    fun setUp() {
        campaniaInsumoRepository = mockk()
        asignarInsumoACampaniaUseCase = AsignarInsumoACampaniaUseCase(campaniaInsumoRepository)
    }

    @Test
    fun `invoke with valid data inserts asignacion`() = runTest {
        // Given
        val idCampania = 1
        val idInsumo = 2
        val cantidad = 5.0
        val precio = 100.0

        coEvery { campaniaInsumoRepository.asignarInsumo(any()) } returns Unit

        // When
        asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad, precio)

        // Then
        coVerify(exactly = 1) { 
            campaniaInsumoRepository.asignarInsumo(withArg {
                assertEquals(idCampania, it.idCampania)
                assertEquals(idInsumo, it.idInsumo)
                assertEquals(cantidad, it.cantidad, 0.0)
                assertEquals(precio, it.precio, 0.0)
            }) 
        }
    }

    @Test
    fun `invoke with invalid cantidad throws exception`() = runTest {
        // Given
        val idCampania = 1
        val idInsumo = 2
        val cantidad = 0.0
        val precio = 100.0

        // When / Then
        try {
            asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad, precio)
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("La cantidad debe ser mayor a cero", e.message)
        }

        coVerify(exactly = 0) { campaniaInsumoRepository.asignarInsumo(any()) }
    }
}
