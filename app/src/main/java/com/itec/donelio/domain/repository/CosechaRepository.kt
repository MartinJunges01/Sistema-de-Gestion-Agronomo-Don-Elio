package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Cosecha
import kotlinx.coroutines.flow.Flow

interface CosechaRepository {
    fun getCosechasByCampania(campaniaId: Int): Flow<List<Cosecha>>

    suspend fun getCosechaById(id: Int): Cosecha?

    suspend fun insertCosecha(cosecha: Cosecha)

    suspend fun updateCosecha(cosecha: Cosecha)

    suspend fun deleteCosecha(cosecha: Cosecha)
}
