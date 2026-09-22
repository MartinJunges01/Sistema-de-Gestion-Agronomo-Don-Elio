package com.itec.donelio.presentation.viewmodel.campania

import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.EliminarCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasActivasUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasInactivasUseCase
import com.itec.donelio.domain.use_case.ReactivarCampaniaUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    private lateinit var reactivarCampaniaUseCase: ReactivarCampaniaUseCase
    private lateinit var viewModel: GestionCampaniasViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val campaniaActiva = Campania(1, "Activa", 100.0, 0L, true, 1, "")
    private val campaniaInactiva = Campania(2, "Inactiva", 100.0, 0L, false, 2, "")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerCampaniasActivasUseCase = mockk()
        obtenerCampaniasInactivasUseCase = mockk()
        eliminarCampaniaUseCase = mockk()
        reactivarCampaniaUseCase = mockk()

        every { obtenerCampaniasActivasUseCase() } returns flowOf(listOf(campaniaActiva))
        every { obtenerCampaniasInactivasUseCase() } returns flowOf(listOf(campaniaInactiva))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = GestionCampaniasViewModel(
        obtenerCampaniasActivasUseCase,
        obtenerCampaniasInactivasUseCase,
        eliminarCampaniaUseCase,
        reactivarCampaniaUseCase
    )

    @Test
    fun `dadoCampaniasActivasEInactivas_cuandoSeObservaActivas_entoncesRetornaSoloActivas`() = runTest {
        // Given
        val activas = listOf(campaniaActiva)
        val inactivas = listOf(campaniaInactiva)

        // When
        viewModel = crearViewModel()

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

    @Test
    fun `dadaCampaniaInactiva_cuandoSeReactiva_entoncesSeInvocaElUseCase`() = runTest {
        // Given
        every { reactivarCampaniaUseCase(campaniaInactiva) } returns flowOf(Resource.Success(Unit))
        viewModel = crearViewModel()
        advanceUntilIdle()

        // When
        viewModel.reactivarCampania(campaniaInactiva)
        advanceUntilIdle()

        // Then
        verify(exactly = 1) { reactivarCampaniaUseCase(campaniaInactiva) }
    }
}
