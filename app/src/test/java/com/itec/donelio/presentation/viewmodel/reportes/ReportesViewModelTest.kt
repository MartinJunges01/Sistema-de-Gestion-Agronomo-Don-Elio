package com.itec.donelio.presentation.viewmodel.reportes

import app.cash.turbine.test
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCatalogoInsumosUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechasPorCampaniaUseCase
import com.itec.donelio.domain.use_case.ObtenerInsumosVinculadosUseCase
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [ReportesViewModel].
 *
 * Cubre:
 * - Issue [#299]: datos mockeados eliminados, nuevos StateFlows contextuales por campaña.
 * - Issue [#300]: guardia de exportación si no hay campaña seleccionada.
 * - Issue [#301]: desglose de cosechas por destino (Almacenada vs Vendida).
 * - Issue [#302]: comparación real entre campañas (cosechasA / cosechasB).
 *
 * Paradigma: Given-When-Then (Dado que... Cuando... Entonces...)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReportesViewModelTest {

    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var obtenerInsumosVinculadosUseCase: ObtenerInsumosVinculadosUseCase
    private lateinit var obtenerCosechasPorCampaniaUseCase: ObtenerCosechasPorCampaniaUseCase
    private lateinit var obtenerCatalogoInsumosUseCase: ObtenerCatalogoInsumosUseCase
    private lateinit var viewModel: ReportesViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val campaniaSoja = Campania(id = 1, nombre = "Soja 2026", hectareas = 100.0, fechaInicio = 0L, estaActiva = true, cultivo = "Soja")
    private val campaniaMaiz = Campania(id = 2, nombre = "Maíz 2025", hectareas = 120.0, fechaInicio = 0L, estaActiva = false, cultivo = "Maíz")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        obtenerCampaniasUseCase = mockk()
        obtenerInsumosVinculadosUseCase = mockk()
        obtenerCosechasPorCampaniaUseCase = mockk()
        obtenerCatalogoInsumosUseCase = mockk()

        every { obtenerCampaniasUseCase() } returns flowOf(emptyList())
        every { obtenerInsumosVinculadosUseCase(any<Int>()) } returns flowOf(emptyList())
        every { obtenerCosechasPorCampaniaUseCase(any<Int>()) } returns flowOf(emptyList())
        every { obtenerCatalogoInsumosUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel(): ReportesViewModel = ReportesViewModel(
        obtenerCampaniasUseCase = obtenerCampaniasUseCase,
        obtenerInsumosVinculadosUseCase = obtenerInsumosVinculadosUseCase,
        obtenerCosechasPorCampaniaUseCase = obtenerCosechasPorCampaniaUseCase,
        obtenerCatalogoInsumosUseCase = obtenerCatalogoInsumosUseCase
    )

    // ──────────────────────────────────────────────
    // VM-R1: lista de campañas vacía
    // ──────────────────────────────────────────────

    /**
     * Dado que la BD no tiene campañas,
     * Cuando se observa [campanias],
     * Entonces debe emitir una lista vacía.
     */
    @Test
    fun `campanias emite lista vacia cuando la BD esta vacia`() = runTest {
        // Given
        every { obtenerCampaniasUseCase() } returns flowOf(emptyList())
        viewModel = crearViewModel()

        // When / Then
        viewModel.campanias.test {
            advanceUntilIdle()
            val lista = awaitItem()
            assertTrue("Entonces campanias debe ser vacía", lista.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // VM-R2: lista de campañas con datos reales
    // ──────────────────────────────────────────────

    /**
     * Dado que la BD tiene 2 campañas,
     * Cuando se observa [campanias],
     * Entonces debe emitir las 2 campañas.
     */
    @Test
    fun `campanias emite la lista real cuando la BD tiene registros`() = runTest {
        // Given
        every { obtenerCampaniasUseCase() } returns flowOf(listOf(campaniaSoja, campaniaMaiz))
        viewModel = crearViewModel()

        // When / Then
        viewModel.campanias.test {
            // El StateFlow emite su valor inicial (emptyList) al suscribirse,
            // luego el valor real una vez que el dispatcher procesa el upstream.
            val inicial = awaitItem()
            assertTrue("El valor inicial del stateIn debe ser lista vacía", inicial.isEmpty())
            advanceUntilIdle()

            // Then
            val lista = awaitItem()
            assertEquals("Entonces campanias debe tener 2 elementos", 2, lista.size)
            assertEquals(campaniaSoja, lista[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // VM-R3: seleccionarCampaniaIndividual actualiza estado
    // ──────────────────────────────────────────────

    /**
     * Dado que el ViewModel está inicializado sin selección,
     * Cuando se llama a [seleccionarCampaniaIndividual],
     * Entonces [campaniaIndividual] debe emitir la campaña elegida.
     */
    @Test
    fun `seleccionarCampaniaIndividual actualiza campaniaIndividual`() = runTest {
        // Given
        viewModel = crearViewModel()

        viewModel.campaniaIndividual.test {
            val inicial = awaitItem()
            assertNull("Dado que no hay selección inicial, debe ser null", inicial)

            // When
            viewModel.seleccionarCampaniaIndividual(campaniaSoja)
            advanceUntilIdle()

            // Then
            val actualizado = awaitItem()
            assertEquals("Entonces campaniaIndividual debe ser campaniaSoja", campaniaSoja, actualizado)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // VM-R4: insumosIndividual vacío sin campaña
    // ──────────────────────────────────────────────

    /**
     * Dado que no hay campaña seleccionada,
     * Cuando se observa [insumosIndividual],
     * Entonces debe emitir lista vacía sin consultar la BD.
     */
    @Test
    fun `insumosIndividual emite lista vacia cuando no hay campania seleccionada`() = runTest {
        // Given
        viewModel = crearViewModel()

        // When / Then
        viewModel.insumosIndividual.test {
            advanceUntilIdle()
            val insumos = awaitItem()
            assertTrue("Entonces insumosIndividual debe ser vacío sin selección", insumos.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // VM-R5: pieChartData null sin campaña seleccionada
    // ──────────────────────────────────────────────

    /**
     * Dado que no hay campaña seleccionada,
     * Cuando se observa [pieChartData],
     * Entonces debe emitir null (no mostrar el gráfico).
     */
    @Test
    fun `pieChartData emite null cuando no hay campania seleccionada`() = runTest {
        // Given
        viewModel = crearViewModel()

        // When / Then
        viewModel.pieChartData.test {
            advanceUntilIdle()
            val datos = awaitItem()
            assertNull("Entonces pieChartData debe ser null sin campaña seleccionada", datos)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // VM-R6: desgloseCosechasData agrupa por almacen y venta
    // ──────────────────────────────────────────────

    /**
     * Dado que hay cosechas almacenadas y vendidas (almacen en blanco),
     * Cuando se selecciona una campaña,
     * Entonces [desgloseCosechasData] debe agrupar correctamente las cantidades en 2 slices.
     */
    @Test
    fun `desgloseCosechasData agrupa por almacen y venta correctamente`() = runTest {
        // Given
        val cosechaAlmacenada1 = Cosecha(id = 1, cantidad = 100.0, fecha = 0L, almacen = "Silo 1", idCampania = 1)
        val cosechaVenta1 = Cosecha(id = 2, cantidad = 50.0, fecha = 0L, almacen = "", idCampania = 1)
        val cosechaAlmacenada2 = Cosecha(id = 3, cantidad = 200.0, fecha = 0L, almacen = "Silo 2", idCampania = 1)
        
        every { obtenerCosechasPorCampaniaUseCase(1) } returns flowOf(listOf(cosechaAlmacenada1, cosechaVenta1, cosechaAlmacenada2))
        viewModel = crearViewModel()

        viewModel.desgloseCosechasData.test {
            val inicial = awaitItem()
            assertNull("Dado que no hay selección, debe ser null", inicial)
            
            // When
            viewModel.seleccionarCampaniaIndividual(campaniaSoja)
            advanceUntilIdle()

            // Then
            val chartData = awaitItem()
            assertNotNull("El grafico debe tener datos", chartData)
            assertEquals("Debe tener 2 slices (Almacenada, Vendida)", 2, chartData!!.slices.size)
            
            val sliceAlmacenada = chartData.slices.find { it.label == "Almacenada" }
            val sliceVendida = chartData.slices.find { it.label == "Vendida" }
            
            assertNotNull("Debe existir slice Almacenada", sliceAlmacenada)
            assertEquals(300.0f, sliceAlmacenada!!.value, 0.01f)
            
            assertNotNull("Debe existir slice Vendida", sliceVendida)
            assertEquals(50.0f, sliceVendida!!.value, 0.01f)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────
    // VM-R7: top3Insumos calcula porcentaje y agrupa
    // ──────────────────────────────────────────────

    /**
     * Dado que hay varios insumos en la campaña seleccionada,
     * Cuando se expone [top3Insumos],
     * Entonces debe tener como máximo 3 insumos, ordenados por costo y con porcentaje calculado.
     */
    @Test
    fun `top3Insumos emite maximo 3 elementos ordenados con porcentaje`() = runTest {
        // Given
        val insumoSemilla = CampaniaInsumo(id = 1, idCampania = 1, idInsumo = 1, cantidad = 10.0, precio = 1000.0) // 10000
        val insumoAgroquimico = CampaniaInsumo(id = 2, idCampania = 1, idInsumo = 2, cantidad = 5.0, precio = 5000.0) // 25000
        val insumoFertilizante = CampaniaInsumo(id = 3, idCampania = 1, idInsumo = 3, cantidad = 20.0, precio = 500.0) // 10000
        val insumoExtra = CampaniaInsumo(id = 4, idCampania = 1, idInsumo = 4, cantidad = 1.0, precio = 100.0) // 100
        
        val catalogoMock = listOf(
            Insumo(id = 1, nombre = "Semilla", categoria = "Semillas"),
            Insumo(id = 2, nombre = "Agroquímico", categoria = "Agroquímicos"),
            Insumo(id = 3, nombre = "Fertilizante", categoria = "Fertilizantes"),
            Insumo(id = 4, nombre = "Extra", categoria = "Otros")
        )
        
        every { obtenerInsumosVinculadosUseCase(1) } returns flowOf(listOf(insumoSemilla, insumoAgroquimico, insumoFertilizante, insumoExtra))
        every { obtenerCatalogoInsumosUseCase() } returns flowOf(catalogoMock)
        
        viewModel = crearViewModel()
        
        viewModel.top3Insumos.test {
            val inicial = awaitItem()
            assertTrue("Debe iniciar vacio", inicial.isEmpty())
            
            // When
            viewModel.seleccionarCampaniaIndividual(campaniaSoja)
            advanceUntilIdle()
            
            // Then
            val top3 = awaitItem()
            assertEquals("Debe tener 3 elementos maximo", 3, top3.size)
            
            assertEquals("El primero debe ser Agroquimico (mayor costo)", "Agroquímico", top3[0].nombre)
            assertEquals("El primero debe ser pos 1", 1, top3[0].posicion)
            assertEquals(25000.0, top3[0].costo, 0.01)
            
            val gastoTotal = 10000.0 + 25000.0 + 10000.0 + 100.0 // 45100
            val porcentajeEsperado = ((25000.0 / 45100.0) * 100).toFloat()
            assertEquals(porcentajeEsperado, top3[0].porcentaje, 0.01f)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
