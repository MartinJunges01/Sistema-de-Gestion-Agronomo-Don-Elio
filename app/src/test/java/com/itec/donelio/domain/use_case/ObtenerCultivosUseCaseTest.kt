package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.repository.CultivoRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObtenerCultivosUseCaseTest {
    private lateinit var repository: CultivoRepository
    private lateinit var useCase: ObtenerCultivosUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObtenerCultivosUseCase(repository)
    }

    @Test
    fun `invoke con soloActivos true llama a getCultivosActivos`() = runTest {
        val mockList = listOf(Cultivo(1, "Soja", true))
        every { repository.getCultivosActivos() } returns flowOf(mockList)

        val result = useCase(soloActivos = true).first()

        assertEquals(mockList, result)
        verify(exactly = 1) { repository.getCultivosActivos() }
        verify(exactly = 0) { repository.getTodosLosCultivos() }
    }

    @Test
    fun `invoke con soloActivos false llama a getTodosLosCultivos`() = runTest {
        val mockList = listOf(Cultivo(1, "Soja", true), Cultivo(2, "Trigo", false))
        every { repository.getTodosLosCultivos() } returns flowOf(mockList)

        val result = useCase(soloActivos = false).first()

        assertEquals(mockList, result)
        verify(exactly = 0) { repository.getCultivosActivos() }
        verify(exactly = 1) { repository.getTodosLosCultivos() }
    }
}
