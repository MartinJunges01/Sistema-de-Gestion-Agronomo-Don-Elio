package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class EliminarCosechaUseCaseTest {
    private lateinit var repository: CosechaRepository
    private lateinit var eliminarCosechaUseCase: EliminarCosechaUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        eliminarCosechaUseCase = EliminarCosechaUseCase(repository)
    }

    @Test
    fun `dado cosecha existente, cuando invoke, entonces repositorio llama deleteCosecha`() = runBlocking {
        val cosecha = Cosecha(id = 1, idCampania = 1, cantidad = 100.0, fecha = 1000L, almacen = "Silo A")
        
        eliminarCosechaUseCase(cosecha)
        
        coVerify(exactly = 1) { repository.deleteCosecha(cosecha) }
    }
}
