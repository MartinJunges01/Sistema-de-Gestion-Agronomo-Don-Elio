package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.SavedStateHandle
import com.itec.donelio.domain.use_case.CrearInsumoCatalogoUseCase
import com.itec.donelio.domain.use_case.EditarInsumoCatalogoUseCase
import com.itec.donelio.domain.use_case.ObtenerInsumoPorIdUseCase
import com.itec.donelio.domain.use_case.ValidarInsumoUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [FormularioInsumoViewModel].
 *
 * Cubre los Acceptance Criteria del Issue [#403]:
 * "Botón Guardar permanece deshabilitado al crear insumo nuevo".
 *
 * Paradigma: Given-When-Then (Dado que... Cuando... Entonces...)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FormularioInsumoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var crearInsumoCatalogoUseCase: CrearInsumoCatalogoUseCase
    private lateinit var editarInsumoCatalogoUseCase: EditarInsumoCatalogoUseCase
    private lateinit var obtenerInsumoPorIdUseCase: ObtenerInsumoPorIdUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        crearInsumoCatalogoUseCase = mockk(relaxed = true)
        editarInsumoCatalogoUseCase = mockk(relaxed = true)
        obtenerInsumoPorIdUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Construye el ViewModel en modo Alta (sin insumoId) con un ValidarInsumoUseCase real.
     * El use case real garantiza que la validacion funciona end-to-end sin mocks.
     */
    private fun crearViewModelAlta(): FormularioInsumoViewModel {
        val handle = SavedStateHandle()
        return FormularioInsumoViewModel(
            crearInsumoCatalogoUseCase = crearInsumoCatalogoUseCase,
            editarInsumoCatalogoUseCase = editarInsumoCatalogoUseCase,
            obtenerInsumoPorIdUseCase = obtenerInsumoPorIdUseCase,
            validarInsumoUseCase = ValidarInsumoUseCase(),
            savedStateHandle = handle
        )
    }

    // ──────────────────────────────────────────────
    // Casos de prueba: AC del Issue #403
    // ──────────────────────────────────────────────

    /**
     * VM-I-1: Dado que el formulario esta en modo Alta (insumoId = null),
     * Cuando el ViewModel se inicializa,
     * Entonces isGuardarHabilitado debe ser false.
     */
    @Test
    fun `estado inicial en modo alta tiene isGuardarHabilitado en false`() {
        // Given / When
        val viewModel = crearViewModelAlta()

        // Then
        assertFalse(
            "VM-I-1: isGuardarHabilitado debe iniciar en false en modo alta",
            viewModel.state.value.isGuardarHabilitado
        )
    }

    /**
     * VM-I-2: Dado que el formulario esta en modo Alta,
     * Cuando el usuario tipea solo el nombre (categoria vacia),
     * Entonces isGuardarHabilitado debe seguir en false.
     */
    @Test
    fun `tipear solo nombre no habilita el boton guardar`() {
        // Given
        val viewModel = crearViewModelAlta()

        // When
        viewModel.onNombreChange("Herbicida")

        // Then
        assertFalse(
            "VM-I-2: isGuardarHabilitado debe ser false si categoria sigue vacia",
            viewModel.state.value.isGuardarHabilitado
        )
    }

    /**
     * VM-I-3: Dado que el formulario esta en modo Alta,
     * Cuando el usuario tipea solo la categoria (nombre vacio),
     * Entonces isGuardarHabilitado debe seguir en false.
     */
    @Test
    fun `tipear solo categoria no habilita el boton guardar`() {
        // Given
        val viewModel = crearViewModelAlta()

        // When
        viewModel.onCategoriaChange("Pesticidas")

        // Then
        assertFalse(
            "VM-I-3: isGuardarHabilitado debe ser false si nombre sigue vacio",
            viewModel.state.value.isGuardarHabilitado
        )
    }

    /**
     * VM-I-4: Dado que el formulario esta en modo Alta,
     * Cuando el usuario tipea un nombre valido Y una categoria valida,
     * Entonces isGuardarHabilitado debe ser true.
     *
     * AC Principal del Issue #403.
     */
    @Test
    fun `tipear nombre y categoria validos habilita el boton guardar`() {
        // Given
        val viewModel = crearViewModelAlta()

        // When
        viewModel.onNombreChange("Herbicida Total")
        viewModel.onCategoriaChange("Pesticidas")

        // Then
        assertTrue(
            "VM-I-4: isGuardarHabilitado debe ser true cuando nombre y categoria son validos",
            viewModel.state.value.isGuardarHabilitado
        )
        assertNull(
            "VM-I-4: errorNombre debe ser null cuando el nombre es valido",
            viewModel.state.value.errorNombre
        )
        assertNull(
            "VM-I-4: errorCategoria debe ser null cuando la categoria es valida",
            viewModel.state.value.errorCategoria
        )
    }

    /**
     * VM-I-5: Dado que el formulario tiene nombre y categoria validos,
     * Cuando el usuario borra el nombre,
     * Entonces isGuardarHabilitado debe volver a false.
     */
    @Test
    fun `borrar nombre deshabilita el boton guardar`() {
        // Given
        val viewModel = crearViewModelAlta()
        viewModel.onNombreChange("Herbicida")
        viewModel.onCategoriaChange("Pesticidas")
        assertTrue(viewModel.state.value.isGuardarHabilitado)

        // When
        viewModel.onNombreChange("")

        // Then
        assertFalse(
            "VM-I-5: isGuardarHabilitado debe ser false cuando el nombre se vacia",
            viewModel.state.value.isGuardarHabilitado
        )
    }
}
