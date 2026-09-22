package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.use_case.EliminarCosechaUseCase
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechasNoAlmacenadasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechasPorCampaniaUseCase
import com.itec.donelio.presentation.state.UltimaSeleccionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
 * Tests unitarios para [CosechaViewModel].
 *
 * Cubre el fix del Issue [#441]: la race condition entre [UltimaSeleccionManager]
 * y el campaniaId explícito de [SavedStateHandle], y el nuevo método [CosechaViewModel.sincronizarCampania].
 *
 * Paradigma: Given-When-Then (Dado que... Cuando... Entonces...)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CosechaViewModelTest {

    private lateinit var obtenerCosechasPorCampaniaUseCase: ObtenerCosechasPorCampaniaUseCase
    private lateinit var obtenerCosechasNoAlmacenadasUseCase: ObtenerCosechasNoAlmacenadasUseCase
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var eliminarCosechaUseCase: EliminarCosechaUseCase
    private lateinit var mockManager: UltimaSeleccionManager

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerCosechasPorCampaniaUseCase = mockk()
        obtenerCosechasNoAlmacenadasUseCase = mockk()
        obtenerCampaniasUseCase = mockk()
        eliminarCosechaUseCase = mockk()
        mockManager = mockk(relaxed = true)

        every { obtenerCampaniasUseCase() } returns flowOf(emptyList<Campania>())
        every { obtenerCosechasPorCampaniaUseCase(any()) } returns flowOf(emptyList<Cosecha>())
        every { obtenerCosechasNoAlmacenadasUseCase(any()) } returns flowOf(emptyMap())
        every { mockManager.campaniaIdSeleccionada } returns MutableStateFlow(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Construye el ViewModel con un SavedStateHandle configurable por test. */
    private fun crearViewModel(campaniaIdEnHandle: Int? = null): CosechaViewModel {
        val handle = if (campaniaIdEnHandle != null) {
            SavedStateHandle(mapOf("campaniaId" to campaniaIdEnHandle))
        } else {
            SavedStateHandle()
        }
        return CosechaViewModel(
            savedStateHandle = handle,
            ultimaSeleccionManager = mockManager,
            obtenerCosechasPorCampaniaUseCase = obtenerCosechasPorCampaniaUseCase,
            obtenerCosechasNoAlmacenadasUseCase = obtenerCosechasNoAlmacenadasUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            eliminarCosechaUseCase = eliminarCosechaUseCase
        )
    }

    // ──────────────────────────────────────────────
    // Fix #441: prioridad SavedState sobre manager
    // ──────────────────────────────────────────────

    /**
     * Dado que el ViewModel se crea con campaniaId = 5 en SavedState,
     * Cuando el UltimaSeleccionManager emite id = 3,
     * Entonces campaniaIdSeleccionada debe permanecer en 5 (no sobreescribirse).
     */
    @Test
    fun `campaniaId explicito en SavedState no se sobreescribe por el manager`() = runTest {
        // Given: manager emite ID 3, pero el handle ya tiene ID 5
        val managerFlow = MutableStateFlow<Int?>(null)
        every { mockManager.campaniaIdSeleccionada } returns managerFlow

        val vm = crearViewModel(campaniaIdEnHandle = 5)
        advanceUntilIdle()

        // When: el manager emite un ID diferente
        managerFlow.value = 3
        advanceUntilIdle()

        // Then: el ID sigue siendo 5
        assertEquals(
            "El campaniaId explícito del SavedState no debe sobreescribirse por el manager",
            5, vm.campaniaIdSeleccionada.value
        )
    }

    /**
     * Dado que el ViewModel se crea con campaniaId = 5 en SavedState,
     * Cuando se inicializa,
     * Entonces debe notificar al manager con campaniaId = 5.
     */
    @Test
    fun `campaniaId explicito en SavedState notifica al manager`() = runTest {
        // Given / When
        crearViewModel(campaniaIdEnHandle = 5)
        advanceUntilIdle()

        // Then: notifica al manager con el ID explícito
        verify { mockManager.seleccionarCampania(5) }
    }

    /**
     * Dado que el ViewModel se crea sin campaniaId en SavedState,
     * Cuando el UltimaSeleccionManager emite id = 7,
     * Entonces campaniaIdSeleccionada debe actualizarse a 7 (fallback BottomNav).
     */
    @Test
    fun `sin campaniaId en SavedState el manager actua como fallback`() = runTest {
        // Given: manager sin ID inicial
        val managerFlow = MutableStateFlow<Int?>(null)
        every { mockManager.campaniaIdSeleccionada } returns managerFlow

        val vm = crearViewModel(campaniaIdEnHandle = null)

        vm.campaniaIdSeleccionada.test {
            awaitItem() // null inicial

            // When
            managerFlow.value = 7
            advanceUntilIdle()

            // Then
            val idActualizado = awaitItem()
            assertEquals("El manager debe actuar como fallback cuando no hay ID explícito", 7, idActualizado)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // sincronizarCampania()
    // ──────────────────────────────────────────────

    /**
     * Dado que el ViewModel inicia sin campaniaId,
     * Cuando se llama a sincronizarCampania(2),
     * Entonces campaniaIdSeleccionada debe emitir 2.
     */
    @Test
    fun `sincronizarCampania actualiza el id cuando difiere del actual`() = runTest {
        // Given
        val vm = crearViewModel(campaniaIdEnHandle = null)

        vm.campaniaIdSeleccionada.test {
            assertNull("Estado inicial debe ser null", awaitItem())

            // When
            vm.sincronizarCampania(2)
            advanceUntilIdle()

            // Then
            assertEquals("Entonces campaniaIdSeleccionada debe valer 2", 2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Dado que el ViewModel tiene campaniaId = 4,
     * Cuando se llama a sincronizarCampania(4),
     * Entonces NO debe emitir un nuevo evento (idempotente).
     */
    @Test
    fun `sincronizarCampania no emite si el id es igual al actual`() = runTest {
        // Given
        val vm = crearViewModel(campaniaIdEnHandle = 4)

        vm.campaniaIdSeleccionada.test {
            assertEquals("Estado inicial debe ser 4", 4, awaitItem())

            // When
            vm.sincronizarCampania(4)
            advanceUntilIdle()

            // Then: ningún nuevo item
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Dado que no hay campaniaId válido,
     * Cuando se observa isCampaniaValid,
     * Entonces debe ser false.
     */
    @Test
    fun `isCampaniaValid emite false cuando campaniaId es nulo`() = runTest {
        // Given / When / Then
        val vm = crearViewModel(campaniaIdEnHandle = null)
        vm.isCampaniaValid.test {
            advanceUntilIdle()
            assertFalse("isCampaniaValid debe ser false sin ID", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Dado que se llama sincronizarCampania(1),
     * Cuando se observa isCampaniaValid,
     * Entonces debe emitir true.
     */
    @Test
    fun `isCampaniaValid emite true tras sincronizarCampania con id valido`() = runTest {
        // Given
        val vm = crearViewModel(campaniaIdEnHandle = null)
        vm.isCampaniaValid.test {
            awaitItem() // false inicial

            // When
            vm.sincronizarCampania(1)
            advanceUntilIdle()

            // Then
            assertTrue("isCampaniaValid debe ser true tras sincronizarCampania(1)", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
