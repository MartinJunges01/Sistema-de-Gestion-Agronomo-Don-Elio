package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class EditarCampaniaInsumoUseCaseTest {
    private lateinit var useCase: EditarCampaniaInsumoUseCase
    private val repository: CampaniaInsumoRepository = mockk()

    @Before
    fun setup() {
        useCase = EditarCampaniaInsumoUseCase(repository)
    }

    @Test
    fun `editar insumo valido invoca actualizarInsumo`() = runBlocking {
        val insumo = CampaniaInsumo(id = 1, idCampania = 1, idInsumo = 1, nombreInsumo = "Test", insumoActivo = true, cantidad = 10.0, precio = 500.0)
        coEvery { repository.actualizarInsumo(insumo) } returns Unit

        useCase(insumo)

        coVerify { repository.actualizarInsumo(insumo) }
    }

    @Test
    fun `editar insumo sin id lanza excepcion`() {
        val insumo = CampaniaInsumo(id = 0, idCampania = 1, idInsumo = 1, nombreInsumo = "Test", insumoActivo = true, cantidad = 10.0, precio = 500.0)
        
        val ex = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(insumo) }
        }
        assertEquals("El insumo a editar debe tener un ID válido", ex.message)
    }

    @Test
    fun `editar insumo con cantidad invalida lanza excepcion`() {
        val insumo = CampaniaInsumo(id = 1, idCampania = 1, idInsumo = 1, nombreInsumo = "Test", insumoActivo = true, cantidad = 0.0, precio = 500.0)
        
        val ex = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(insumo) }
        }
        assertEquals("La cantidad debe ser mayor a 0", ex.message)
    }
}


