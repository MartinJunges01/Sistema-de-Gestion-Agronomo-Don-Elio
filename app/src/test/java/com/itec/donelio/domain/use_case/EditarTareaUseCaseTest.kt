package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.core.alarm.TaskReminderScheduler
import com.itec.donelio.domain.repository.TareaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditarTareaUseCaseTest {

    private lateinit var tareaRepository: TareaRepository
    private lateinit var taskReminderScheduler: TaskReminderScheduler
    private lateinit var editarTareaUseCase: EditarTareaUseCase

    @Before
    fun setUp() {
        tareaRepository = mockk()
        taskReminderScheduler = mockk(relaxed = true)
        editarTareaUseCase = EditarTareaUseCase(tareaRepository, taskReminderScheduler)
    }

    @Test
    fun `invoke with valid data updates and schedules if notify is true`() = runTest {
        val tarea = Tarea(id = 1, nombre = "Regar", fecha = 1L, hora = "10:00", notificar = true, confirmar = false, idCampania = 1)
        
        coEvery { tareaRepository.updateTarea(any()) } returns Unit

        editarTareaUseCase(tarea).test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 1) { tareaRepository.updateTarea(tarea) }
        verify(exactly = 1) { taskReminderScheduler.schedule(tarea) }
    }

    @Test
    fun `invoke with valid data updates and cancels if notify is false`() = runTest {
        val tarea = Tarea(id = 1, nombre = "Regar", fecha = 1L, hora = "10:00", notificar = false, confirmar = false, idCampania = 1)
        
        coEvery { tareaRepository.updateTarea(any()) } returns Unit

        editarTareaUseCase(tarea).test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 1) { tareaRepository.updateTarea(tarea) }
        verify(exactly = 1) { taskReminderScheduler.cancel(tarea.id) }
    }

    @Test
    fun `invoke with blank name emits error`() = runTest {
        val tarea = Tarea(id = 1, nombre = "   ", fecha = 1L, hora = "10:00", notificar = true, confirmar = false, idCampania = 1)

        editarTareaUseCase(tarea).test {
            assertTrue(awaitItem() is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertEquals("El nombre de la tarea no puede estar vacío", (result as Resource.Error).message)
            awaitComplete()
        }

        coVerify(exactly = 0) { tareaRepository.updateTarea(any()) }
        verify(exactly = 0) { taskReminderScheduler.schedule(any()) }
    }
}
