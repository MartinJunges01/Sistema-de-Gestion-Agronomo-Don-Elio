package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.use_case.CrearTareaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NuevaTareaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var crearTareaUseCase: CrearTareaUseCase
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: NuevaTareaViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        crearTareaUseCase = mockk()
        obtenerCampaniasUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("campaniaId" to 1))

        coEvery { obtenerCampaniasUseCase() } returns flowOf(emptyList())

        viewModel = NuevaTareaViewModel(
            savedStateHandle = savedStateHandle,
            crearTareaUseCase = crearTareaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardar tarea con hora vacia muestra error`() {
        viewModel.onNombreChange("Tarea test")
        viewModel.onHoraChange("")
        viewModel.guardar()

        val state = viewModel.state.value
        assertEquals("La hora es obligatoria", state.errorHora)
    }

    @Test
    fun `guardar tarea con hora invalida muestra error`() {
        viewModel.onNombreChange("Tarea test")
        viewModel.onHoraChange("25:00") // Invalido
        viewModel.guardar()

        var state = viewModel.state.value
        assertEquals("Formato inválido (HH:mm)", state.errorHora)

        viewModel.onHoraChange("12:60") // Invalido
        viewModel.guardar()
        state = viewModel.state.value
        assertEquals("Formato inválido (HH:mm)", state.errorHora)

        viewModel.onHoraChange("abc") // Invalido
        viewModel.guardar()
        state = viewModel.state.value
        assertEquals("Formato inválido (HH:mm)", state.errorHora)
    }

    @Test
    fun `guardar tarea con hora valida pasa validacion de hora`() {
        coEvery { crearTareaUseCase(any(), any(), any(), any(), any()) } returns flowOf(Resource.Success(Unit))

        viewModel.onNombreChange("Tarea test")
        
        // Caso normal
        viewModel.onHoraChange("14:30")
        viewModel.guardar()
        var state = viewModel.state.value
        assertEquals(null, state.errorHora)
        
        // Caso límite inferior
        viewModel.onHoraChange("00:00")
        viewModel.guardar()
        state = viewModel.state.value
        assertEquals(null, state.errorHora)
        
        // Caso límite superior
        viewModel.onHoraChange("23:59")
        viewModel.guardar()
        state = viewModel.state.value
        assertEquals(null, state.errorHora)
    }
}
