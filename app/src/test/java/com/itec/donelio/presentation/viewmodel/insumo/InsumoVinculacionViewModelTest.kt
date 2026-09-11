package com.itec.donelio.presentation.viewmodel.insumo

import androidx.lifecycle.SavedStateHandle
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.*
import com.itec.donelio.presentation.state.UltimaSeleccionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class InsumoVinculacionViewModelTest {

    private lateinit var viewModel: InsumoVinculacionViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    private val ultimaSeleccionManager: UltimaSeleccionManager = mockk(relaxed = true)
    private val obtenerInsumosVinculadosUseCase: ObtenerInsumosVinculadosUseCase = mockk()
    private val obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase = mockk()
    private val asignarInsumoACampaniaUseCase: AsignarInsumoACampaniaUseCase = mockk()
    private val desvincularInsumoUseCase: DesvincularInsumoUseCase = mockk()
    private val obtenerCampaniasUseCase: ObtenerCampaniasUseCase = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { ultimaSeleccionManager.campaniaIdSeleccionada } returns MutableStateFlow(null)
        every { obtenerInsumosVinculadosUseCase(any()) } returns emptyFlow()
        every { obtenerCatalogoInsumosUseCase() } returns emptyFlow()
        every { obtenerCampaniasUseCase() } returns emptyFlow()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(savedStateCampaniaId: Int? = null) {
        val savedStateHandle = SavedStateHandle().apply {
            savedStateCampaniaId?.let { set("campaniaId", it) }
        }
        viewModel = InsumoVinculacionViewModel(
            savedStateHandle = savedStateHandle,
            ultimaSeleccionManager = ultimaSeleccionManager,
            obtenerInsumosVinculadosUseCase = obtenerInsumosVinculadosUseCase,
            obtenerCatalogoInsumosUseCase = obtenerCatalogoInsumosUseCase,
            asignarInsumoACampaniaUseCase = asignarInsumoACampaniaUseCase,
            desvincularInsumoUseCase = desvincularInsumoUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase
        )
    }

    /**
     * Dado un InsumoVinculacionViewModel con una campaña seleccionada,
     * Cuando se llama a asignarInsumo(),
     * Entonces llama a AsignarInsumoACampaniaUseCase con los parametros correctos.
     */
    @Test
    fun `asignarInsumo llama al UseCase con campaniaId correcto`() = runTest {
        // Given
        val campaniaId = 5
        coEvery { asignarInsumoACampaniaUseCase(any(), any(), any(), any()) } returns Unit
        createViewModel(campaniaId)
        
        // When
        viewModel.asignarInsumo(idInsumo = 10, cantidad = 50.0, precio = 1500.0)
        advanceUntilIdle()
        
        // Then
        coVerify(exactly = 1) { 
            asignarInsumoACampaniaUseCase(
                idCampania = campaniaId,
                idInsumo = 10,
                cantidad = 50.0,
                precio = 1500.0
            ) 
        }
    }
}
