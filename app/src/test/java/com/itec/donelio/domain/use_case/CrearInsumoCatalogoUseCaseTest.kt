package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CrearInsumoCatalogoUseCaseTest {

    private lateinit var useCase: CrearInsumoCatalogoUseCase
    private val insumoRepository: InsumoRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = CrearInsumoCatalogoUseCase(insumoRepository)
    }

    @Test
    fun `Given insumo valido When invoke Then repositorio insertarInsumo es llamado`() = runBlocking {
        // Given
        val nombre = "Semilla"
        val categoria = "Categoria 1"
        val icono = "??"

        // When
        useCase(nombre, categoria, icono)

        // Then
        coVerify(exactly = 1) { 
            insumoRepository.insertInsumo(match { 
                it.nombre == nombre && it.categoria == categoria && it.icono == icono && it.id == 0 
            }) 
        }
    }

    @Test
    fun `Given nombre vacio When invoke Then lanza IllegalArgumentException`() = runBlocking {
        // Given
        val nombre = "   "
        val categoria = "Categoria"

        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(nombre, categoria) }
        }
        assertTrue(exception.message?.contains("vac") == true)
    }
}

