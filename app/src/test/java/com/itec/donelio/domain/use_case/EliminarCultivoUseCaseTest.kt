package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.repository.CultivoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EliminarCultivoUseCaseTest {
    private lateinit var repository: CultivoRepository
    private lateinit var useCase: EliminarCultivoUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = EliminarCultivoUseCase(repository)
    }

    @Test
    fun `invoke llama a deleteCultivo en el repositorio`() = runTest {
        useCase(1)
        coVerify(exactly = 1) { repository.deleteCultivo(1) }
    }
}
