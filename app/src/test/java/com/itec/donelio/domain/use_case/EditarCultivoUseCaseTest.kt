package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.repository.CultivoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EditarCultivoUseCaseTest {
    private lateinit var repository: CultivoRepository
    private lateinit var useCase: EditarCultivoUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = EditarCultivoUseCase(repository)
    }

    @Test
    fun `invoke con nombre valido actualiza cultivo`() = runTest {
        val cultivo = Cultivo(id = 1, nombre = " Soja ", activo = true)
        useCase(cultivo)
        
        coVerify(exactly = 1) { repository.updateCultivo(match { it.nombre == "Soja" }) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke con nombre vacio lanza excepcion`() = runTest {
        val cultivoInvalido = Cultivo(id = 1, nombre = "   ", activo = true)
        useCase(cultivoInvalido)
    }
}
