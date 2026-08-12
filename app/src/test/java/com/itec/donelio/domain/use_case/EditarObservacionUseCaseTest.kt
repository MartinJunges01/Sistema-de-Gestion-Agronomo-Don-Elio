package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.ObservacionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditarObservacionUseCaseTest {

    private lateinit var observacionRepository: ObservacionRepository
    private lateinit var useCase: EditarObservacionUseCase

    @Before
    fun setUp() {
        observacionRepository = mockk(relaxed = true)
        useCase = EditarObservacionUseCase(observacionRepository)
    }

    @Test
    fun `editar con texto vacio y sin imagen devuelve error`() = runTest {
        val observacionInvalida = Observacion(1, "", null, 1)
        val result = useCase(observacionInvalida).toList()

        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("La observación debe tener texto o una foto", (result[1] as Resource.Error).message)
    }

    @Test
    fun `editar con texto valido llama al repositorio y devuelve success`() = runTest {
        val observacionValida = Observacion(1, "Texto editado", null, 1)
        val result = useCase(observacionValida).toList()

        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success)
    }
}
