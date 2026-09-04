package com.itec.donelio.presentation.viewmodel.observacion

import androidx.lifecycle.SavedStateHandle
import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.EditarObservacionUseCase
import com.itec.donelio.domain.use_case.EliminarObservacionUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerObservacionesPorCampaniaUseCase
import io.mockk.coEvery
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
class ObservacionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var obtenerObservacionesPorCampaniaUseCase: ObtenerObservacionesPorCampaniaUseCase
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var editarObservacionUseCase: EditarObservacionUseCase
    private lateinit var eliminarObservacionUseCase: EliminarObservacionUseCase
    private lateinit var validarObservacionUseCase: com.itec.donelio.domain.use_case.ValidarObservacionUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: ObservacionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerObservacionesPorCampaniaUseCase = mockk()
        obtenerCampaniasUseCase = mockk()
        editarObservacionUseCase = mockk()
        eliminarObservacionUseCase = mockk()
        validarObservacionUseCase = com.itec.donelio.domain.use_case.ValidarObservacionUseCase()
        savedStateHandle = SavedStateHandle(mapOf("campaniaId" to 1))

        every { obtenerCampaniasUseCase() } returns flowOf(emptyList())
        every { obtenerObservacionesPorCampaniaUseCase(any()) } returns flowOf(emptyList())

        val mockManager = mockk<com.itec.donelio.presentation.state.UltimaSeleccionManager>(relaxed = true)
        every { mockManager.campaniaIdSeleccionada } returns kotlinx.coroutines.flow.MutableStateFlow(null)

        viewModel = ObservacionViewModel(
            savedStateHandle = savedStateHandle,
            ultimaSeleccionManager = mockManager,
            obtenerObservacionesPorCampaniaUseCase = obtenerObservacionesPorCampaniaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            editarObservacionUseCase = editarObservacionUseCase,
            eliminarObservacionUseCase = eliminarObservacionUseCase,
            validarObservacionUseCase = validarObservacionUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `editar observacion con error actualiza errorMessage`() = runTest(testDispatcher) {
        val observacion = Observacion(1, "", null, 1)
        coEvery { editarObservacionUseCase(any()) } returns flowOf(Resource.Error("Texto vacio"))

        viewModel.editarObservacion(observacion)
        advanceUntilIdle()

        assertEquals("Texto vacio", viewModel.errorMessage.value)
    }

    @Test
    fun `eliminar observacion con error actualiza errorMessage`() = runTest(testDispatcher) {
        val observacion = Observacion(1, "Texto", null, 1)
        coEvery { eliminarObservacionUseCase(any()) } returns flowOf(Resource.Error("Error al eliminar"))

        viewModel.eliminarObservacion(observacion)
        advanceUntilIdle()

        assertEquals("Error al eliminar", viewModel.errorMessage.value)
    }
}
