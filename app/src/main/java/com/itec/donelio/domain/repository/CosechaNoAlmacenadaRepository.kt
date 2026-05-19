package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.CosechaNoAlmacenada
import kotlinx.coroutines.flow.Flow

interface CosechaNoAlmacenadaRepository {
    fun getNoAlmacenadasPorCampania(idCampania: Int): Flow<List<CosechaNoAlmacenada>>
    suspend fun getPorCosechaId(cosechaId: Int): CosechaNoAlmacenada?
    suspend fun insert(cosechaNoAlmacenada: CosechaNoAlmacenada)
    suspend fun delete(cosechaNoAlmacenada: CosechaNoAlmacenada)
}
