package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Cultivo
import kotlinx.coroutines.flow.Flow

interface CultivoRepository {
    fun getCultivosActivos(): Flow<List<Cultivo>>
    fun getTodosLosCultivos(): Flow<List<Cultivo>>
    suspend fun getCultivoById(id: Int): Cultivo?
    suspend fun insertCultivo(cultivo: Cultivo): Long
    suspend fun updateCultivo(cultivo: Cultivo)
    suspend fun deleteCultivo(id: Int)
}
