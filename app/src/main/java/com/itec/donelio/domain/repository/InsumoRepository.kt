package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Insumo
import kotlinx.coroutines.flow.Flow

interface InsumoRepository {
    fun getAllInsumos(): Flow<List<Insumo>>

    suspend fun getInsumoById(id: Int): Insumo?

    suspend fun insertInsumo(insumo: Insumo)

    suspend fun updateInsumo(insumo: Insumo)

    suspend fun deleteInsumo(insumo: Insumo)
}
