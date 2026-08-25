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

class CrearCampaniaUseCaseTest {

    private lateinit var campaniaRepository: CampaniaRepository
    private lateinit var crearCampaniaUseCase: CrearCampaniaUseCase

    @Before
    fun setUp() {
        campaniaRepository = mockk()
        crearCampaniaUseCase = CrearCampaniaUseCase(campaniaRepository)
    }

    @Test
    fun `invoke with valid data inserts campania and returns Success`() = runTest {
        // Given
        val nombre = "Trigo de Invierno"
        val hectareas = 100.0
        val cultivo = "Trigo"
        val fechaInicio = 1680000000000L
        
        coEvery { campaniaRepository.insertCampania(any()) } returns Unit

        // When
        crearCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio).test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            
            awaitComplete()
        }

        coVerify(exactly = 1) { 
            campaniaRepository.insertCampania(withArg {
                assertEquals(nombre, it.nombre)
                assertEquals(hectareas, it.hectareas, 0.0)
                assertEquals(cultivo, it.cultivo)
                assertEquals(fechaInicio, it.fechaInicio)
                assertTrue(it.estaActiva)
            })
        }
    }

    @Test
    fun `invoke with blank name throws exception and returns Error`() = runTest {
        // Given
        val nombre = "  "
        val hectareas = 100.0
        val cultivo = "Trigo"
        val fechaInicio = 1680000000000L

        // When
        crearCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio).test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals("El nombre de la campaña no puede estar vacío", (error as Resource.Error).message)
            
            awaitComplete()
        }

        coVerify(exactly = 0) { campaniaRepository.insertCampania(any()) }
    }
}
