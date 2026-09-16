package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AsignarInsumoACampaniaUseCaseTest {

    private lateinit var campaniaInsumoRepository: CampaniaInsumoRepository
    private lateinit var asignarInsumoACampaniaUseCase: AsignarInsumoACampaniaUseCase

    @Before
    fun setUp() {
        campaniaInsumoRepository = mockk()
        asignarInsumoACampaniaUseCase = AsignarInsumoACampaniaUseCase(campaniaInsumoRepository)
    }

    @Test
    fun `invoke with valid data inserts asignacion`() = runTest {
        // Given
        val idCampania = 1
        val idInsumo = 2
        val cantidad = 5.0
        val precio = 100.0

        coEvery { campaniaInsumoRepository.asignarInsumo(any()) } returns Unit

        // When
        asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad, precio)

        // Then
        coVerify(exactly = 1) {
            campaniaInsumoRepository.asignarInsumo(withArg {
                assertEquals(idCampania, it.idCampania)
                assertEquals(idInsumo, it.idInsumo)
                assertEquals(cantidad, it.cantidad, 0.0)
                assertEquals(precio, it.precio, 0.0)
            })
        }
    }

    @Test
    fun `invoke with invalid cantidad throws exception`() = runTest {
        // Given
        val idCampania = 1
        val idInsumo = 2
        val cantidad = 0.0
        val precio = 100.0

        // When / Then
        try {
            asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad, precio)
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("La cantidad debe ser mayor a cero", e.message)
        }

        coVerify(exactly = 0) { campaniaInsumoRepository.asignarInsumo(any()) }
    }

    /**
     * [#455] Given: mismo insumo vinculado dos veces a la misma campaña
     * When: se llama al UseCase dos veces con el mismo idCampania e idInsumo
     * Then: el repositorio recibe dos llamadas separadas (no se acumula, no hay lógica de merge)
     */
    @Test
    fun `invoke llamado dos veces con mismo insumo crea dos registros independientes`() = runTest {
        // Given
        val idCampania = 1
        val idInsumo = 5
        coEvery { campaniaInsumoRepository.asignarInsumo(any()) } returns Unit

        // When
        asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad = 5.0, precio = 100.0)
        asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad = 3.0, precio = 150.0)

        // Then: se llamó dos veces al repositorio, no una sola vez con suma
        coVerify(exactly = 2) { campaniaInsumoRepository.asignarInsumo(any()) }
    }

    /**
     * [#455] La fecha de aplicación se asigna automáticamente (no es cero) al momento de invocar.
     */
    @Test
    fun `invoke asigna fechaAplicacion automaticamente`() = runTest {
        // Given
        val idCampania = 1
        val idInsumo = 3
        val capturado = slot<CampaniaInsumo>()
        coEvery { campaniaInsumoRepository.asignarInsumo(capture(capturado)) } returns Unit
        val tiempoAntes = System.currentTimeMillis()

        // When
        asignarInsumoACampaniaUseCase(idCampania, idInsumo, cantidad = 2.0, precio = 50.0)

        // Then: fechaAplicacion es un timestamp reciente (no cero)
        assertTrue(
            "fechaAplicacion debe ser mayor a 0",
            capturado.captured.fechaAplicacion > 0L
        )
        assertTrue(
            "fechaAplicacion debe ser mayor o igual al tiempo antes de la llamada",
            capturado.captured.fechaAplicacion >= tiempoAntes
        )
    }
}
