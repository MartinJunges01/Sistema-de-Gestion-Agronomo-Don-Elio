package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.core.alarm.TaskReminderScheduler
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.repository.TareaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfirmarTareaUseCaseTest {

    private lateinit var tareaRepository: TareaRepository
    private lateinit var taskReminderScheduler: TaskReminderScheduler
    private lateinit var confirmarTareaUseCase: ConfirmarTareaUseCase

    @Before
    fun setUp() {
        tareaRepository = mockk(relaxed = true)
        taskReminderScheduler = mockk(relaxed = true)
        confirmarTareaUseCase = ConfirmarTareaUseCase(tareaRepository, taskReminderScheduler)
    }

    @Test
    fun `invoke with completada true cancels scheduler and returns Success`() = runTest {
        // Given
        val tareaId = 1
        val completada = true

        coEvery { tareaRepository.completeTarea(any(), any()) } returns Unit

        // When
        confirmarTareaUseCase(tareaId, completada).test {
            // Then
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 1) { tareaRepository.completeTarea(tareaId, completada) }
        verify(exactly = 1) { taskReminderScheduler.cancel(tareaId) }
    }

    @Test
    fun `invoke with completada false does not cancel scheduler`() = runTest {
        // Given
        val tareaId = 1
        val completada = false

        coEvery { tareaRepository.completeTarea(any(), any()) } returns Unit

        // When
        confirmarTareaUseCase(tareaId, completada).test {
            // Then
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 1) { tareaRepository.completeTarea(tareaId, completada) }
        verify(exactly = 0) { taskReminderScheduler.cancel(any()) }
    }
}
