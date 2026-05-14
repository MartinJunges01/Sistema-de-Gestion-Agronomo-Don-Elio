package com.itec.donelio.domain.repository

import com.itec.donelio.domain.model.Observacion
import kotlinx.coroutines.flow.Flow

interface ObservacionRepository {
    fun getObservacionesPorCampania(idCampania: Int): Flow<List<Observacion>>

    suspend fun insertObservacion(observacion: Observacion)

    suspend fun updateObservacion(observacion: Observacion)

    suspend fun deleteObservacion(observacion: Observacion)
}
