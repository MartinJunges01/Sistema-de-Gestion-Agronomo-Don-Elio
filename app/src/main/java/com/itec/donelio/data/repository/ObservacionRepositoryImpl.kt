package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.repository.ObservacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservacionRepositoryImpl @Inject constructor(
    private val observacionDao: ObservacionDao
) : ObservacionRepository {

    override fun getObservacionesPorCampania(idCampania: Int): Flow<List<Observacion>> {
        return observacionDao.getObservacionesPorCampania(idCampania).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertObservacion(observacion: Observacion) {
        observacionDao.insertObservacion(observacion.toEntity())
    }

    override suspend fun updateObservacion(observacion: Observacion) {
        observacionDao.updateObservacion(observacion.toEntity())
    }

    override suspend fun deleteObservacion(observacion: Observacion) {
        observacionDao.deleteObservacion(observacion.toEntity())
    }
}
