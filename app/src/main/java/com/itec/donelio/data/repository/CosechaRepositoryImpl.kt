package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.repository.CosechaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CosechaRepositoryImpl @Inject constructor(
    private val cosechaDao: CosechaDao
) : CosechaRepository {

    override fun getCosechasByCampania(campaniaId: Int): Flow<List<Cosecha>> {
        return cosechaDao.getCosechasPorCampania(campaniaId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCosechaById(id: Int): Cosecha? {
        return cosechaDao.getCosechaById(id)?.toDomain()
    }

    override suspend fun insertCosecha(cosecha: Cosecha) {
        cosechaDao.insertCosecha(cosecha.toEntity())
    }

    override suspend fun updateCosecha(cosecha: Cosecha) {
        cosechaDao.updateCosecha(cosecha.toEntity())
    }

    override suspend fun deleteCosecha(cosecha: Cosecha) {
        cosechaDao.deleteCosecha(cosecha.toEntity())
    }
}
