package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.CampaniaInsumo
import kotlinx.coroutines.flow.Flow

interface CampaniaInsumoRepository {
    fun getInsumosUtilizadosEnCampania(idCampania: Int): Flow<List<CampaniaInsumo>>
    
    fun getAllInsumosUtilizados(): Flow<List<CampaniaInsumo>>

    suspend fun asignarInsumo(campaniaInsumo: CampaniaInsumo)

    suspend fun desvincularInsumo(campaniaInsumo: CampaniaInsumo)
}
