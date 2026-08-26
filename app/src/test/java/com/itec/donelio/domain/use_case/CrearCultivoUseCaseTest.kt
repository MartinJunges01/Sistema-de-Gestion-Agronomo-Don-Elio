package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CultivoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CrearCultivoUseCaseTest {
    private lateinit var repository: CultivoRepository
    private lateinit var useCase: CrearCultivoUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = CrearCultivoUseCase(repository)
    }

    @Test
    fun `invoke con nombre valido inserta cultivo y retorna id`() = runTest {
        coEvery { repository.insertCultivo(any()) } returns 1L

        val result = useCase("Soja")

        assertEquals(1L, result)
        coVerify(exactly = 1) { repository.insertCultivo(match { it.nombre == "Soja" && it.activo }) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke con nombre vacio lanza excepcion`() = runTest {
        useCase("   ")
    }
}
