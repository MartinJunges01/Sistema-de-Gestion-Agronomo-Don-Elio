package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.model.CampaniaInsumo
import kotlinx.coroutines.flow.Flow

interface InsumoRepository {
    fun getInsumosVinculadosACampania(campaniaId: Int): Flow<List<CampaniaInsumo>>

    fun getAllInsumosVinculados(): Flow<List<CampaniaInsumo>>

    fun getAllInsumos(): Flow<List<Insumo>>

    suspend fun getInsumoById(id: Int): Insumo?

    suspend fun insertInsumo(insumo: Insumo)

    suspend fun updateInsumo(insumo: Insumo)

    suspend fun deleteInsumo(insumo: Insumo)


}
