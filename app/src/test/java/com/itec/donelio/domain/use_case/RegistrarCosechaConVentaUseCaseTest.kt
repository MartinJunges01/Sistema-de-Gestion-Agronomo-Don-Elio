package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class RegistrarCosechaConVentaUseCaseTest {

    private lateinit var cosechaRepository: CosechaRepository
    private lateinit var noAlmacenadaRepository: CosechaNoAlmacenadaRepository
    private lateinit var registrarCosechaConVentaUseCase: RegistrarCosechaConVentaUseCase

    @Before
    fun setUp() {
        cosechaRepository = mockk()
        noAlmacenadaRepository = mockk()
        registrarCosechaConVentaUseCase = RegistrarCosechaConVentaUseCase(cosechaRepository, noAlmacenadaRepository)
    }

    @Test
    fun `invoke with valid data inserts into both repositories`() = runTest {
        // Given
        val cantidad = 100.0
        val fecha = 1680000000000L
        val idCampania = 1
        val tipo = "Venta"
        val precio = 50.0

        val idCosechaGenerado = 10L
        coEvery { cosechaRepository.insertCosecha(any()) } returns idCosechaGenerado
        coEvery { noAlmacenadaRepository.insert(any()) } returns Unit

        registrarCosechaConVentaUseCase(cantidad, fecha, idCampania, tipo, precio)

        // Then
        coVerify(exactly = 1) { 
            cosechaRepository.insertCosecha(withArg {
                assertEquals(cantidad, it.cantidad, 0.0)
                assertEquals(fecha, it.fecha)
                assertEquals("", it.almacen)
                assertEquals(idCampania, it.idCampania)
            }) 
        }

        coVerify(exactly = 1) { 
            noAlmacenadaRepository.insert(withArg {
                assertEquals(tipo, it.tipo)
                assertEquals(precio, it.precio, 0.0)
                assertEquals(idCosechaGenerado.toInt(), it.idCosecha)
            }) 
        }
    }

    @Test
    fun `invoke with invalid cantidad throws exception`() = runTest {
        // Given
        val cantidad = 0.0
        val fecha = 1680000000000L
        val idCampania = 1
        val tipo = "Venta"
        val precio = 50.0

        // When / Then
        try {
            registrarCosechaConVentaUseCase(cantidad, fecha, idCampania, tipo, precio)
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("La cantidad debe ser mayor a cero", e.message)
        }

        coVerify(exactly = 0) { cosechaRepository.insertCosecha(any()) }
        coVerify(exactly = 0) { noAlmacenadaRepository.insert(any()) }
    }
}
