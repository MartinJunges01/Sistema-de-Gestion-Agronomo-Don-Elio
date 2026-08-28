package com.itec.donelio.presentation.viewmodel.cosecha

import androidx.lifecycle.SavedStateHandle
import com.itec.donelio.domain.use_case.ObtenerCampaniasUseCase
import com.itec.donelio.domain.use_case.ObtenerCosechaPorIdUseCase
import com.itec.donelio.domain.use_case.EditarCosechaUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaConVentaUseCase
import com.itec.donelio.domain.use_case.RegistrarCosechaUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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

@OptIn(ExperimentalCoroutinesApi::class)
class FormularioCosechaViewModelTest {

    private lateinit var registrarCosechaUseCase: RegistrarCosechaUseCase
    private lateinit var registrarConVentaUseCase: RegistrarCosechaConVentaUseCase
    private lateinit var obtenerCampaniasUseCase: ObtenerCampaniasUseCase
    private lateinit var obtenerCosechaPorIdUseCase: ObtenerCosechaPorIdUseCase
    private lateinit var editarCosechaUseCase: EditarCosechaUseCase
    private lateinit var validarDatosCosechaUseCase: com.itec.donelio.domain.use_case.ValidarDatosCosechaUseCase
    private lateinit var viewModel: FormularioCosechaViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        registrarCosechaUseCase = mockk()
        registrarConVentaUseCase = mockk()
        obtenerCampaniasUseCase = mockk()
        obtenerCosechaPorIdUseCase = mockk()
        editarCosechaUseCase = mockk()
        validarDatosCosechaUseCase = mockk()
        every { obtenerCampaniasUseCase() } returns flowOf(emptyList())
        coEvery { obtenerCosechaPorIdUseCase(any()) } returns null
        every { validarDatosCosechaUseCase(any(), any(), any(), any()) } returns com.itec.donelio.domain.util.ValidationResult.Success
        viewModel = FormularioCosechaViewModel(
            savedStateHandle = SavedStateHandle(),
            registrarCosechaUseCase = registrarCosechaUseCase,
            registrarConVentaUseCase = registrarConVentaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            obtenerCosechaPorIdUseCase = obtenerCosechaPorIdUseCase,
            editarCosechaUseCase = editarCosechaUseCase,
            validarDatosCosechaUseCase = validarDatosCosechaUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // Test 1: Sin campaña seleccionada → errorCampania, sin llamada a Use Cases
    // -------------------------------------------------------------------------
    @Test
    fun `guardar sin campania seleccionada setea errorCampania y no llama a los use cases`() = runTest {
        // Given: sin campaniaId en SavedStateHandle y cantidad válida
        viewModel.onCantidadChange("100")

        // When
        viewModel.guardar()

        // Then
        assertEquals("Debe seleccionar una campaña", viewModel.state.value.errorCampania)
        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.guardadoExitoso)
        coVerify(exactly = 0) { registrarCosechaUseCase(any(), any(), any(), any()) }
        coVerify(exactly = 0) { registrarConVentaUseCase(any(), any(), any(), any(), any()) }
    }

    // -------------------------------------------------------------------------
    // Test 2: Con campaña seleccionada pero cantidad vacía → errorCantidad
    // -------------------------------------------------------------------------
    @Test
    fun `guardar con cantidad vacia setea errorCantidad y no llama a los use cases`() = runTest {
        // Given: campaña seleccionada, cantidad vacía
        every { validarDatosCosechaUseCase(any(), any(), any(), any()) } returns com.itec.donelio.domain.util.ValidationResult.Error("La cantidad debe ser mayor a 0.")
        viewModel.onCampaniaChange(1)

        // When
        viewModel.guardar()

        // Then
        assertEquals("La cantidad debe ser mayor a 0.", viewModel.state.value.errorCantidad)
        assertNull(viewModel.state.value.errorCampania)
        coVerify(exactly = 0) { registrarCosechaUseCase(any(), any(), any(), any()) }
    }

    // -------------------------------------------------------------------------
    // Test 3: Precio inválido (no numérico) → errorPrecio, sin guardar
    // -------------------------------------------------------------------------
    @Test
    fun `onPrecioChange con valor no numerico setea errorPrecio y bloquea guardar`() = runTest {
        // Given: precio no numérico
        viewModel.onCampaniaChange(1)
        viewModel.onCantidadChange("100")
        viewModel.onAlmacenadoChange(false)
        viewModel.onTipoChange("Venta")
        viewModel.onPrecioChange("abc")

        // When
        viewModel.guardar()

        // Then
        assertEquals("Precio inválido", viewModel.state.value.errorPrecio)
        assertFalse(viewModel.state.value.guardadoExitoso)
        coVerify(exactly = 0) { registrarConVentaUseCase(any(), any(), any(), any(), any()) }
    }

    // -------------------------------------------------------------------------
    // Test 4: Datos válidos + almacenado → llama a RegistrarCosechaUseCase(4 args)
    // -------------------------------------------------------------------------
    @Test
    fun `guardar con datos validos y almacenado llama a registrarCosechaUseCase`() = runTest {
        // Given: campaña, cantidad y almacén válidos
        coEvery { registrarCosechaUseCase(any(), any(), any(), any()) } just runs
        viewModel.onCampaniaChange(1)
        viewModel.onCantidadChange("100")
        viewModel.onAlmacenChange("Silo 1")

        // When
        viewModel.guardar()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.guardadoExitoso)
        assertNull(viewModel.state.value.errorCantidad)
        assertNull(viewModel.state.value.errorCampania)
        coVerify(exactly = 1) {
            registrarCosechaUseCase(100.0, any(), "Silo 1", 1)
        }
    }

    // -------------------------------------------------------------------------
    // Test 5: Datos válidos + venta → llama a RegistrarCosechaConVentaUseCase(5 args)
    // -------------------------------------------------------------------------
    @Test
    fun `guardar con datos validos y venta llama a registrarConVentaUseCase`() = runTest {
        // Given: campaña, cantidad, tipo y precio válidos, sin almacenar
        coEvery { registrarConVentaUseCase(any(), any(), any(), any(), any()) } just runs
        viewModel.onCampaniaChange(1)
        viewModel.onCantidadChange("100")
        viewModel.onAlmacenadoChange(false)
        viewModel.onTipoChange("Venta")
        viewModel.onPrecioChange("500")

        // When
        viewModel.guardar()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.guardadoExitoso)
        coVerify(exactly = 1) {
            registrarConVentaUseCase(100.0, any(), 1, "Venta", 500.0)
        }
    }
    // -------------------------------------------------------------------------
    // Test 6 (#335): Init con cosechaId válido → cargarCosecha() carga datos en state
    // -------------------------------------------------------------------------
    @Test
    fun `init con cosechaId valido carga la cosecha en el estado`() = runTest {
        // Given: cosechaId = 7 en SavedStateHandle
        val cosechaFalsa = com.itec.donelio.domain.model.Cosecha(
            id = 7, idCampania = 2, cantidad = 55.0,
            fecha = 1_700_000_000_000L, almacen = "Silo A"
        )
        coEvery { obtenerCosechaPorIdUseCase(7) } returns cosechaFalsa

        val vmConId = FormularioCosechaViewModel(
            savedStateHandle = SavedStateHandle(mapOf("cosechaId" to 7, "campaniaId" to 2)),
            registrarCosechaUseCase = registrarCosechaUseCase,
            registrarConVentaUseCase = registrarConVentaUseCase,
            obtenerCampaniasUseCase = obtenerCampaniasUseCase,
            obtenerCosechaPorIdUseCase = obtenerCosechaPorIdUseCase,
            editarCosechaUseCase = editarCosechaUseCase,
            validarDatosCosechaUseCase = validarDatosCosechaUseCase
        )
        advanceUntilIdle()

        // Then: el state refleja los datos de la cosecha cargada
        assertEquals(7, vmConId.state.value.cosechaId)
        assertEquals("55.0", vmConId.state.value.cantidad)
        assertEquals("Silo A", vmConId.state.value.almacen)
        assertEquals(2, vmConId.state.value.campaniaId)
        assertTrue(vmConId.state.value.almacenado)
    }

    // -------------------------------------------------------------------------
    // Test 7 (#336): Error del UseCase con mensaje "cantidad" → errorCantidad, errorFecha null
    // -------------------------------------------------------------------------
    @Test
    fun `guardar con error de cantidad setea errorCantidad y errorFecha permanece null`() = runTest {
        // Given: UseCase retorna error de cantidad
        every { validarDatosCosechaUseCase(any(), any(), any(), any()) } returns
            com.itec.donelio.domain.util.ValidationResult.Error("La cantidad debe ser mayor a 0.")
        viewModel.onCampaniaChange(1)

        // When
        viewModel.guardar()

        // Then
        assertEquals("La cantidad debe ser mayor a 0.", viewModel.state.value.errorCantidad)
        assertNull(viewModel.state.value.errorFecha)
    }

    // -------------------------------------------------------------------------
    // Test 8 (#336): Error del UseCase con mensaje "fecha" → errorFecha, errorCantidad null
    // -------------------------------------------------------------------------
    @Test
    fun `guardar con error de fecha setea errorFecha y errorCantidad permanece null`() = runTest {
        // Given: UseCase retorna error de fecha
        every { validarDatosCosechaUseCase(any(), any(), any(), any()) } returns
            com.itec.donelio.domain.util.ValidationResult.Error("La fecha es obligatoria.")
        viewModel.onCampaniaChange(1)
        viewModel.onCantidadChange("50")

        // When
        viewModel.guardar()

        // Then
        assertEquals("La fecha es obligatoria.", viewModel.state.value.errorFecha)
        assertNull(viewModel.state.value.errorCantidad)
    }

    // -------------------------------------------------------------------------
    // Test 9 (#336): onFechaChange limpia errorFecha
    // -------------------------------------------------------------------------
    @Test
    fun `onFechaChange limpia errorFecha`() = runTest {
        // Given: hay un errorFecha en el state
        every { validarDatosCosechaUseCase(any(), any(), any(), any()) } returns
            com.itec.donelio.domain.util.ValidationResult.Error("La fecha es obligatoria.")
        viewModel.onCampaniaChange(1)
        viewModel.onCantidadChange("50")
        viewModel.guardar()
        assertEquals("La fecha es obligatoria.", viewModel.state.value.errorFecha)

        // When
        viewModel.onFechaChange(System.currentTimeMillis())

        // Then
        assertNull(viewModel.state.value.errorFecha)
    }
}
