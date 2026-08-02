package com.itec.donelio.domain.use_case

import app.cash.turbine.test
import com.itec.donelio.core.alarm.TaskReminderScheduler
import com.itec.donelio.domain.model.Resource
import com.itec.donelio.domain.model.Tarea
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

class CrearTareaUseCaseTest {

    private lateinit var tareaRepository: TareaRepository
    private lateinit var taskReminderScheduler: TaskReminderScheduler
    private lateinit var crearTareaUseCase: CrearTareaUseCase

    @Before
    fun setUp() {
        tareaRepository = mockk()
        taskReminderScheduler = mockk(relaxed = true)
        crearTareaUseCase = CrearTareaUseCase(tareaRepository, taskReminderScheduler)
    }

    @Test
    fun `invoke with valid data inserts tarea and schedules reminder if notificar is true`() = runTest {
        // Given
        val nombre = "Fumigar campo"
        val fechaOriginal = 1680012345678L // Fecha con horas, minutos y milisegundos
        val fechaNormalizadaEsperada = java.util.Calendar.getInstance().apply {
            timeInMillis = fechaOriginal
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        val hora = "08:00"
        val notificar = true
        val idCampania = 1
        
        val expectedId = 5L
        coEvery { tareaRepository.insertTarea(any()) } returns expectedId

        // When
        crearTareaUseCase(nombre, fechaOriginal, hora, notificar, idCampania).test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            
            awaitComplete()
        }

        coVerify(exactly = 1) { 
            tareaRepository.insertTarea(withArg {
                assertEquals(nombre, it.nombre)
                assertEquals(fechaNormalizadaEsperada, it.fecha)
                assertEquals(hora, it.hora)
                assertTrue(it.notificar)
                assertEquals(idCampania, it.idCampania)
            })
        }
        
        verify(exactly = 1) { 
            taskReminderScheduler.schedule(withArg {
                assertEquals(expectedId.toInt(), it.id)
                assertEquals(nombre, it.nombre)
            })
        }
    }

    @Test
    fun `invoke with blank name throws exception and returns Error`() = runTest {
        // Given
        val nombre = "   "
        val fecha = 1680000000000L
        val hora = "08:00"
        val notificar = true
        val idCampania = 1

        // When
        crearTareaUseCase(nombre, fecha, hora, notificar, idCampania).test {
            // Then
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals("El nombre de la tarea no puede estar vacío", (error as Resource.Error).message)
            
            awaitComplete()
        }

        coVerify(exactly = 0) { tareaRepository.insertTarea(any()) }
        verify(exactly = 0) { taskReminderScheduler.schedule(any()) }
    }
}
