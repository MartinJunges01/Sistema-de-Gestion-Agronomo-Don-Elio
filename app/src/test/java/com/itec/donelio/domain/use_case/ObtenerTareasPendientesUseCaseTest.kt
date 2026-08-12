package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObtenerTareasPendientesUseCaseTest {

    private lateinit var tareaRepository: TareaRepository
    private lateinit var useCase: ObtenerTareasPendientesUseCase

    @Before
    fun setUp() {
        tareaRepository = mockk()
        useCase = ObtenerTareasPendientesUseCase(tareaRepository)
    }

    @Test
    fun `invoke llama al repositorio con la fechaLimite y limite proporcionados`() = runTest {
        val tareasMock = listOf(
            Tarea(1, "Tarea 1", System.currentTimeMillis(), "10:00", true, false, 1)
        )
        val fechaLimite = 1000L
        val limite = 5

        every { tareaRepository.getTareasPendientesGlobales(limite, fechaLimite) } returns flowOf(tareasMock)

        val result = useCase(limite = limite, fechaLimite = fechaLimite).first()

        assertEquals(1, result.size)
        assertEquals("Tarea 1", result[0].nombre)
    }
}
