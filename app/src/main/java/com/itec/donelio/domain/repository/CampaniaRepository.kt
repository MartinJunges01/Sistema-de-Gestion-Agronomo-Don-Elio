package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Campania
import kotlinx.coroutines.flow.Flow

interface CampaniaRepository {
    fun getCampanias(): Flow<List<Campania>>
    
    fun getCampaniasActivas(): Flow<List<Campania>>
    
    fun getCampaniasInactivas(): Flow<List<Campania>>

    suspend fun getCampaniaById(id: Int): Campania?

    suspend fun insertCampania(campania: Campania)

    suspend fun updateCampania(campania: Campania)

    suspend fun deleteCampania(campania: Campania)
}
