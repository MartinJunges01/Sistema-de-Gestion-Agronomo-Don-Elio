package com.itec.donelio.presentation.viewmodel.home

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.use_case.ObtenerCampaniasActivasUseCase
import com.itec.donelio.domain.use_case.ObtenerTareasPendientesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var obtenerCampaniasActivasUseCase: ObtenerCampaniasActivasUseCase
    private lateinit var obtenerTareasPendientesUseCase: ObtenerTareasPendientesUseCase
    private lateinit var sessionManager: com.itec.donelio.core.SessionManager
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerCampaniasActivasUseCase = mockk()
        obtenerTareasPendientesUseCase = mockk()
        sessionManager = mockk(relaxed = true)
        every { sessionManager.userName } returns flowOf("Invitado")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `las tareas pendientes se obtienen pasando una fecha limite`() = runTest(testDispatcher) {
        val tareasMock = listOf(Tarea(1, "Tarea reciente", System.currentTimeMillis(), "10:00", true, false, 1))
        
        every { obtenerCampaniasActivasUseCase() } returns flowOf(emptyList())
        // Debe capturar cualquier fecha limite
        every { obtenerTareasPendientesUseCase(5, any()) } returns flowOf(tareasMock)

        viewModel = HomeViewModel(
            obtenerCampaniasActivasUseCase,
            obtenerTareasPendientesUseCase,
            sessionManager
        )

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tareasPendientes.collect {}
        }

        advanceUntilIdle()

        val tareas = viewModel.tareasPendientes.value
        assertEquals(1, tareas.size)
        assertEquals("Tarea reciente", tareas[0].nombre)
        
        collectJob.cancel()
    }
}
