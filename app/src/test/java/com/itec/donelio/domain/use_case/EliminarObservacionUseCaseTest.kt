package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.ObservacionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EliminarObservacionUseCaseTest {

    private lateinit var observacionRepository: ObservacionRepository
    private lateinit var useCase: EliminarObservacionUseCase

    @Before
    fun setUp() {
        observacionRepository = mockk(relaxed = true)
        useCase = EliminarObservacionUseCase(observacionRepository)
    }

    @Test
    fun `eliminar llama al repositorio y devuelve success`() = runTest {
        val observacion = Observacion(1, "Texto", null, 1)
        val result = useCase(observacion).toList()

        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success)
    }

    @Test
    fun `dado que el repositorio falla, cuando se elimina observacion, entonces emite Resource Error`() = runTest {
        val observacion = Observacion(1, "Texto", null, 1)
        coEvery { observacionRepository.deleteObservacion(any()) } throws Exception("DB Error")
        
        val result = useCase(observacion).toList()

        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        org.junit.Assert.assertEquals("Error al eliminar observación: DB Error", (result[1] as Resource.Error).message)
    }
}
