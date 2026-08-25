package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.CultivoDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.domain.repository.CultivoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CultivoRepositoryImpl @Inject constructor(
    private val cultivoDao: CultivoDao
) : CultivoRepository {

    override fun getCultivosActivos(): Flow<List<Cultivo>> {
        return cultivoDao.getCultivosActivos().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTodosLosCultivos(): Flow<List<Cultivo>> {
        return cultivoDao.getTodosLosCultivos().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getCultivoById(id: Int): Cultivo? {
        return cultivoDao.getCultivoById(id)?.toDomain()
    }

    override suspend fun insertCultivo(cultivo: Cultivo): Long {
        return cultivoDao.insertCultivo(cultivo.toEntity())
    }

    override suspend fun updateCultivo(cultivo: Cultivo) {
        cultivoDao.updateCultivo(cultivo.toEntity())
    }

    override suspend fun deleteCultivo(id: Int) {
        cultivoDao.softDeleteCultivo(id)
    }
}
