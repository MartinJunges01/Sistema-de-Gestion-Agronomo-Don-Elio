package com.itec.donelio.presentation.viewmodel.tarea

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.use_case.ConfirmarTareaUseCase
import com.itec.donelio.domain.use_case.EditarTareaUseCase
import com.itec.donelio.domain.use_case.EliminarTareaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerTareasPorCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerTareasDelDiaUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [TareaViewModel].
 *
 * Cubre principalmente el comportamiento de [TareaViewModel.sincronizarCampania],
 * incorporado como fix del Issue [#292] donde la pestaña Tareas en DetalleCampania
 * no se actualizaba al cambiar de campaña por usar una key estática en hiltViewModel().
 *
 * Paradigma: Given-When-Then (Dado que... Cuando... Entonces...)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TareaViewModelTest {

    private lateinit var obtenerTareasPorCampaniaUseCase: ObtenerTareasPorCampaniaUseCase
    private lateinit var obtenerTareasDelDiaUseCase: ObtenerTareasDelDiaUseCase
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var confirmarTareaUseCase: ConfirmarTareaUseCase
    private lateinit var editarTareaUseCase: EditarTareaUseCase
    private lateinit var eliminarTareaUseCase: EliminarTareaUseCase
    private lateinit var viewModel: TareaViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerTareasPorCampaniaUseCase = mockk()
        obtenerTareasDelDiaUseCase = mockk()
        confirmarTareaUseCase = mockk()
        editarTareaUseCase = mockk()
        eliminarTareaUseCase = mockk()
        obtenerCampaniasUseCase = mockk()

        // Se usa receptor explícito para evitar ambigüedad de tipos con invoke()
        every { obtenerCampaniasUseCase() } returns flowOf(emptyList<Campania>())
        every { obtenerTareasDelDiaUseCase(any<Int>(), any<Long>()) } returns flowOf(emptyList<Tarea>())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Construye el ViewModel con un SavedStateHandle configurable por test. */
    private fun crearViewModel(campaniaIdEnHandle: Int? = null): TareaViewModel {
        val handle = if (campaniaIdEnHandle != null) {
            SavedStateHandle(mapOf("campaniaId" to campaniaIdEnHandle))
        } else {
            SavedStateHandle()
        }
        return TareaViewModel(
            savedStateHandle = handle,
            obtenerTareasPorCampaniaUseCase = obtenerTareasPorCampaniaUseCase,
            obtenerTareasDelDiaUseCase = obtenerTareasDelDiaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            confirmarTareaUseCase = confirmarTareaUseCase,
            editarTareaUseCase = editarTareaUseCase,
            eliminarTareaUseCase = eliminarTareaUseCase
        )
    }

    // ──────────────────────────────────────────────
    // Casos de prueba: sincronizarCampania()
    // ──────────────────────────────────────────────

    /**
     * Dado que el ViewModel inicia sin campaniaId en el SavedStateHandle,
     * Cuando se llama a sincronizarCampania(5),
     * Entonces campaniaIdSeleccionada debe emitir el valor 5.
     */
    @Test
    fun `sincronizarCampania actualiza el id cuando difiere del actual`() = runTest {
        // Given
        viewModel = crearViewModel(campaniaIdEnHandle = null)

        viewModel.campaniaIdSeleccionada.test {
            val idInicial = awaitItem()
            assertNull("Dado que no hay id en el handle, el estado inicial debe ser null", idInicial)

            // When
            viewModel.sincronizarCampania(5)
            advanceUntilIdle()

            // Then
            val idActualizado = awaitItem()
            assertEquals("Entonces campaniaIdSeleccionada debe valer 5", 5, idActualizado)

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Dado que el ViewModel ya tiene campaniaId = 5,
     * Cuando se llama a sincronizarCampania(5) con el mismo valor,
     * Entonces el StateFlow NO debe emitir un nuevo valor (sin efectos secundarios).
     */
    @Test
    fun `sincronizarCampania no emite si el id es igual al actual`() = runTest {
        // Given
        viewModel = crearViewModel(campaniaIdEnHandle = 5)

        viewModel.campaniaIdSeleccionada.test {
            val idInicial = awaitItem()
            assertEquals("Dado que el handle tiene id=5, el estado inicial debe ser 5", 5, idInicial)

            // When
            viewModel.sincronizarCampania(5)
            advanceUntilIdle()

            // Then: no debe llegar ningún nuevo item
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // Casos de prueba: tareas y validación
    // ──────────────────────────────────────────────

    /**
     * Dado que el ViewModel inicia sin campaniaId válido,
     * Cuando se observa tareas,
     * Entonces debe emitir una lista vacía (no debe consultar el repositorio).
     */
    @Test
    fun `tareas emite lista vacia si no hay campaniaId valido`() = runTest {
        // Given
        viewModel = crearViewModel(campaniaIdEnHandle = null)

        // When / Then
        viewModel.tareas.test {
            val tareas = awaitItem()
            assertTrue("Entonces tareas debe ser lista vacía sin campaniaId", tareas.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Dado que campaniaIdSeleccionada es null,
     * Cuando se observa isCampaniaValid,
     * Entonces debe emitir false.
     */
    @Test
    fun `isCampaniaValid emite false cuando campaniaId es nulo`() = runTest {
        // Given
        viewModel = crearViewModel(campaniaIdEnHandle = null)

        // When / Then
        viewModel.isCampaniaValid.test {
            advanceUntilIdle()
            val esValida = awaitItem()
            assertFalse("Entonces isCampaniaValid debe ser false cuando campaniaId es null", esValida)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Dado que se llama a sincronizarCampania(3) con un id válido,
     * Cuando se observa isCampaniaValid,
     * Entonces debe emitir true.
     */
    @Test
    fun `isCampaniaValid emite true tras sincronizarCampania con id valido`() = runTest {
        // Given
        viewModel = crearViewModel(campaniaIdEnHandle = null)

        viewModel.isCampaniaValid.test {
            awaitItem() // estado inicial: false

            // When
            viewModel.sincronizarCampania(3)
            advanceUntilIdle()

            // Then
            val esValida = awaitItem()
            assertTrue("Entonces isCampaniaValid debe ser true tras sincronizarCampania(3)", esValida)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
