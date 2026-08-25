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

class EditarCampaniaUseCaseTest {

    private lateinit var campaniaRepository: CampaniaRepository
    private lateinit var editarCampaniaUseCase: EditarCampaniaUseCase

    @Before
    fun setUp() {
        campaniaRepository = mockk()
        editarCampaniaUseCase = EditarCampaniaUseCase(campaniaRepository)
    }

    @Test
    fun `invoke with valid data updates and emits success`() = runTest {
        val campania = Campania(id = 1, nombre = "Nueva", hectareas = 100.0, cultivoId = 1, cultivoNombre = "Soja", fechaInicio = 1L, estaActiva = true)
        
        coEvery { campaniaRepository.updateCampania(any()) } returns Unit

        editarCampaniaUseCase(campania).test {
            assertTrue(awaitItem() is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 1) { campaniaRepository.updateCampania(campania) }
    }

    @Test
    fun `invoke with blank name emits error`() = runTest {
        val campania = Campania(id = 1, nombre = "  ", hectareas = 100.0, cultivoId = 1, cultivoNombre = "Soja", fechaInicio = 1L, estaActiva = true)

        editarCampaniaUseCase(campania).test {
            assertTrue(awaitItem() is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertEquals("El nombre de la campaña no puede estar vacío", (result as Resource.Error).message)
            awaitComplete()
        }

        coVerify(exactly = 0) { campaniaRepository.updateCampania(any()) }
    }
}
