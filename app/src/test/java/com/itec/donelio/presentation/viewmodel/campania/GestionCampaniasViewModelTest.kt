package com.itec.donelio.presentation.viewmodel.campania

import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.use_case.ObtenerCampaniasActivasUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasInactivasUseCase
import com.itec.donelio.domain.use_case.EliminarCampaniaUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GestionCampaniasViewModelTest {

    private lateinit var obtenerCampaniasActivasUseCase: ObtenerCampaniasActivasUseCase
    private lateinit var obtenerCampaniasInactivasUseCase: ObtenerCampaniasInactivasUseCase
    private lateinit var eliminarCampaniaUseCase: EliminarCampaniaUseCase
    private lateinit var viewModel: GestionCampaniasViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerCampaniasActivasUseCase = mockk()
        obtenerCampaniasInactivasUseCase = mockk()
        eliminarCampaniaUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dadoCampaniasActivasEInactivas_cuandoSeObservaActivas_entoncesRetornaSoloActivas`() = runTest {
        // Given
        val activas = listOf(Campania(1, "Activa", 100.0, 0L, true, ""))
        val inactivas = listOf(Campania(2, "Inactiva", 100.0, 0L, false, ""))
        
        every { obtenerCampaniasActivasUseCase() } returns flowOf(activas)
        every { obtenerCampaniasInactivasUseCase() } returns flowOf(inactivas)

        // When
        viewModel = GestionCampaniasViewModel(
            obtenerCampaniasActivasUseCase, 
            obtenerCampaniasInactivasUseCase,
            eliminarCampaniaUseCase
        )

        // Then
        viewModel.campaniasActivas.test {
            assertEquals(emptyList<Campania>(), awaitItem()) // Initial value
            advanceUntilIdle()
            assertEquals(activas, awaitItem())
        }
        
        viewModel.campaniasInactivas.test {
            assertEquals(emptyList<Campania>(), awaitItem()) // Initial value
            advanceUntilIdle()
            assertEquals(inactivas, awaitItem())
        }
    }
}
