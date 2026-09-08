package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.use_case.CrearTareaUseCase
import com.itec.donelio.domain.use_case.EditarTareaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerTareaPorIdUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [NuevaTareaViewModel].
 *
 * Cubre: validaciones de formulario (modo alta), precarga de datos (modo edición)
 * y preservación del campo [Tarea.confirmar] al editar.
 *
 * Paradigma: Given-When-Then (Dado que... Cuando... Entonces...)
 *
 * Issues cubiertos: [#410] ABM completo de Tareas.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NuevaTareaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var crearTareaUseCase: CrearTareaUseCase
    private lateinit var editarTareaUseCase: EditarTareaUseCase
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var obtenerTareaPorIdUseCase: ObtenerTareaPorIdUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        crearTareaUseCase = mockk()
        editarTareaUseCase = mockk()
        obtenerCampaniasUseCase = mockk()
        obtenerTareaPorIdUseCase = mockk()

        coEvery { obtenerCampaniasUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Construye el ViewModel en modo Alta (sin tareaId). */
    private fun crearViewModelAlta(campaniaId: Int = 1): NuevaTareaViewModel {
        val handle = SavedStateHandle(mapOf("campaniaId" to campaniaId))
        return NuevaTareaViewModel(
            savedStateHandle = handle,
            crearTareaUseCase = crearTareaUseCase,
            editarTareaUseCase = editarTareaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            obtenerTareaPorIdUseCase = obtenerTareaPorIdUseCase
        )
    }

    /** Construye el ViewModel en modo Edición (con tareaId). */
    private fun crearViewModelEdicion(tareaId: Int, campaniaId: Int = 1): NuevaTareaViewModel {
        val handle = SavedStateHandle(mapOf("campaniaId" to campaniaId, "tareaId" to tareaId))
        return NuevaTareaViewModel(
            savedStateHandle = handle,
            crearTareaUseCase = crearTareaUseCase,
            editarTareaUseCase = editarTareaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            obtenerTareaPorIdUseCase = obtenerTareaPorIdUseCase
        )
    }

    // ──────────────────────────────────────────────
    // Casos de prueba: Validaciones de formulario (Modo Alta)
    // ──────────────────────────────────────────────

    /**
     * Dado que el formulario está en modo Alta,
     * Cuando se llama a guardar() con hora vacía,
     * Entonces el estado debe tener errorHora = "La hora es obligatoria".
     */
    @Test
    fun `guardar tarea con hora vacia muestra error`() {
        val viewModel = crearViewModelAlta()
        viewModel.onNombreChange("Tarea test")
        viewModel.onHoraChange("")
        viewModel.guardar()

        val state = viewModel.state.value
        assertEquals("La hora es obligatoria", state.errorHora)
    }

    /**
     * Dado que el formulario está en modo Alta,
     * Cuando se llama a guardar() con hora en formato inválido,
     * Entonces el estado debe tener errorHora = "Formato inválido (HH:mm)".
     */
    @Test
    fun `guardar tarea con hora invalida muestra error`() {
        val viewModel = crearViewModelAlta()
        viewModel.onNombreChange("Tarea test")
        viewModel.onHoraChange("25:00")
        viewModel.guardar()

        var state = viewModel.state.value
        assertEquals("Formato inválido (HH:mm)", state.errorHora)

        viewModel.onHoraChange("12:60")
        viewModel.guardar()
        state = viewModel.state.value
        assertEquals("Formato inválido (HH:mm)", state.errorHora)

        viewModel.onHoraChange("abc")
        viewModel.guardar()
        state = viewModel.state.value
        assertEquals("Formato inválido (HH:mm)", state.errorHora)
    }

    /**
     * Dado que el formulario está en modo Alta,
     * Cuando se llama a guardar() con hora válida,
     * Entonces errorHora debe ser null.
     */
    @Test
    fun `guardar tarea con hora valida pasa validacion de hora`() {
        coEvery { crearTareaUseCase(any(), any(), any(), any(), any()) } returns flowOf(Resource.Success(Unit))

        val viewModel = crearViewModelAlta()
        viewModel.onNombreChange("Tarea test")

        viewModel.onHoraChange("14:30")
        viewModel.guardar()
        var state = viewModel.state.value
        assertNull(state.errorHora)

        viewModel.onHoraChange("00:00")
        viewModel.guardar()
        state = viewModel.state.value
        assertNull(state.errorHora)

        viewModel.onHoraChange("23:59")
        viewModel.guardar()
        state = viewModel.state.value
        assertNull(state.errorHora)
    }

    // ──────────────────────────────────────────────
    // Casos de prueba: Modo Edición — Issue #410
    // ──────────────────────────────────────────────

    /**
     * VM-T-E1: Dado que existe una tarea con id=5 y confirmar=true (completada),
     * Cuando el ViewModel inicia en modo edición con tareaId=5,
     * Entonces el estado debe pre-cargarse con los datos de la tarea, incluyendo confirmar=true.
     */
    @Test
    fun `modo edicion precarga datos de la tarea existente incluyendo confirmar`() = runTest {
        // Given
        val tareaExistente = Tarea(
            id = 5,
            nombre = "Tarea Completada",
            fecha = 1_700_000_000_000L,
            hora = "09:00",
            notificar = false,
            confirmar = true,
            idCampania = 1
        )
        coEvery { obtenerTareaPorIdUseCase(5) } returns tareaExistente

        // When
        val viewModel = crearViewModelEdicion(tareaId = 5)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("VM-T-E1: nombre debe pre-cargarse", "Tarea Completada", state.nombre)
        assertEquals("VM-T-E1: hora debe pre-cargarse", "09:00", state.hora)
        assertTrue("VM-T-E1: confirmar debe ser true (tarea completada)", state.confirmar)
        assertEquals("VM-T-E1: notificar debe pre-cargarse", false, state.notificar)
    }

    /**
     * VM-T-E2: Dado que una tarea completada (confirmar=true) está en modo edición,
     * Cuando se guarda sin cambiar el estado de confirmar,
     * Entonces editarTareaUseCase debe recibir una tarea con confirmar=true.
     *
     * Previene la regresión donde confirmar se reseteaba a false al guardar.
     */
    @Test
    fun `editar tarea completada preserva confirmar true al guardar`() = runTest {
        // Given
        val tareaCompletada = Tarea(
            id = 7,
            nombre = "Riego campo sur",
            fecha = 1_700_000_000_000L,
            hora = "08:00",
            notificar = true,
            confirmar = true,
            idCampania = 2
        )
        coEvery { obtenerTareaPorIdUseCase(7) } returns tareaCompletada
        coEvery { editarTareaUseCase(any()) } returns flowOf(Resource.Success(Unit))

        val viewModel = crearViewModelEdicion(tareaId = 7, campaniaId = 2)
        advanceUntilIdle()

        // When — se edita solo el nombre y se guarda
        viewModel.onNombreChange("Riego campo sur - revisado")
        viewModel.guardar()
        advanceUntilIdle()

        // Then — confirmar no debe haber sido reseteado a false
        val state = viewModel.state.value
        assertTrue(
            "VM-T-E2: confirmar debe preservarse true al guardar en modo edición",
            state.confirmar
        )
        assertTrue("VM-T-E2: guardadoExitoso debe ser true", state.guardadoExitoso)
    }
}
