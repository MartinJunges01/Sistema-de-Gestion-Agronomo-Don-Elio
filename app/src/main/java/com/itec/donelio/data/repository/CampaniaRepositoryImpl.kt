package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.repository.CampaniaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CampaniaRepositoryImpl @Inject constructor(
    private val campaniaDao: CampaniaDao
) : CampaniaRepository {

    override fun getCampanias(): Flow<List<Campania>> {
        return campaniaDao.getCampanias().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCampaniaById(id: Int): Campania? {
        return campaniaDao.getCampaniaById(id)?.toDomain()
    }

    override suspend fun insertCampania(campania: Campania) {
        campaniaDao.insertCampania(campania.toEntity())
    }

    override suspend fun updateCampania(campania: Campania) {
        campaniaDao.updateCampania(campania.toEntity())
    }

    override suspend fun deleteCampania(campania: Campania) {
        campaniaDao.deleteCampania(campania.toEntity())
    }
}
