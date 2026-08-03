package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.repository.InsumoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EliminarInsumoCatalogoUseCaseTest {

    private lateinit var insumoRepository: InsumoRepository
    private lateinit var eliminarInsumoCatalogoUseCase: EliminarInsumoCatalogoUseCase

    @Before
    fun setUp() {
        insumoRepository = mockk()
        eliminarInsumoCatalogoUseCase = EliminarInsumoCatalogoUseCase(insumoRepository)
    }

    @Test
    fun `invoke with valid insumo calls deleteInsumo in repository`() = runTest {
        // Given
        val insumo = Insumo(
            id = 1,
            nombre = "Glifosato",
            categoria = "Herbicida"
        )
        
        coEvery { insumoRepository.deleteInsumo(any()) } returns Unit

        // When
        eliminarInsumoCatalogoUseCase(insumo)

        // Then
        coVerify(exactly = 1) { 
            insumoRepository.deleteInsumo(insumo) 
        }
    }
}
