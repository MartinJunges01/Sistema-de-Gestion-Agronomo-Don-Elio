package com.itec.donelio.presentation.viewmodel.cultivo

import app.cash.turbine.test
import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.use_case.CrearCultivoUseCase
import com.itec.donelio.domain.use_case.EditarCultivoUseCase
import com.itec.donelio.domain.use_case.EliminarCultivoUseCase
import com.itec.donelio.domain.use_case.ObtenerCultivosUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CultivoCatalogoViewModelTest {

    private lateinit var obtenerCultivosUseCase: ObtenerCultivosUseCase
    private lateinit var crearCultivoUseCase: CrearCultivoUseCase
    private lateinit var editarCultivoUseCase: EditarCultivoUseCase
    private lateinit var eliminarCultivoUseCase: EliminarCultivoUseCase
    private lateinit var viewModel: CultivoCatalogoViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerCultivosUseCase = mockk()
        crearCultivoUseCase = mockk()
        editarCultivoUseCase = mockk()
        eliminarCultivoUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dadoCultivosActivos_cuandoSeInicializa_entoncesExponeCatalogo`() = runTest {
        // Given
        val cultivos = listOf(
            Cultivo(1, "Soja", true),
            Cultivo(2, "Trigo", true)
        )
        every { obtenerCultivosUseCase(soloActivos = true) } returns flowOf(cultivos)

        // When
        viewModel = CultivoCatalogoViewModel(
            obtenerCultivosUseCase,
            crearCultivoUseCase,
            editarCultivoUseCase,
            eliminarCultivoUseCase
        )

        // Then
        viewModel.catalogo.test {
            assertEquals(emptyList<Cultivo>(), awaitItem())
            advanceUntilIdle()
            assertEquals(cultivos, awaitItem())
        }
    }

    @Test
    fun `cuandoCrearCultivoExitoso_entoncesLlamaUseCase`() = runTest {
        // Given
        every { obtenerCultivosUseCase(any()) } returns flowOf(emptyList())
        coEvery { crearCultivoUseCase(any()) } returns 1L

        viewModel = CultivoCatalogoViewModel(
            obtenerCultivosUseCase,
            crearCultivoUseCase,
            editarCultivoUseCase,
            eliminarCultivoUseCase
        )

        // When
        viewModel.crearCultivo("Soja")
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { crearCultivoUseCase("Soja") }
    }

    @Test
    fun `cuandoCrearCultivoFalla_entoncesActualizaError`() = runTest {
        // Given
        every { obtenerCultivosUseCase(any()) } returns flowOf(emptyList())
        coEvery { crearCultivoUseCase(any()) } throws RuntimeException("Error BD")

        viewModel = CultivoCatalogoViewModel(
            obtenerCultivosUseCase,
            crearCultivoUseCase,
            editarCultivoUseCase,
            eliminarCultivoUseCase
        )

        // When
        viewModel.crearCultivo("Soja")
        advanceUntilIdle()

        // Then
        viewModel.error.test {
            assertEquals("Error BD", awaitItem())
        }
    }
}
