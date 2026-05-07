package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Tarea
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun getTareasByCampania(campaniaId: Int): Flow<List<Tarea>>

    suspend fun getTareaById(id: Int): Tarea?

    suspend fun insertTarea(tarea: Tarea)

    suspend fun updateTarea(tarea: Tarea)

    suspend fun deleteTarea(tarea: Tarea)

    suspend fun completeTarea(tareaId: Int, completada: Boolean)
}
