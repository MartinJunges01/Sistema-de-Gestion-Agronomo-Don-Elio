package com.itec.donelio.domain.repository

interface TareaRepository {
    // El dominio pide Tareas (Modelos), no TareaEntities
    fun getTareasByCampania(campaniaId: Int): Flow<List<Tarea>>

    suspend fun insertTarea(tarea: Tarea)

    suspend fun completeTarea(tareaId: Int, completada: Boolean)
}