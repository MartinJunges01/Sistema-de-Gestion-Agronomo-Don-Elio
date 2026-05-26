package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.InsumoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InsumoRepositoryImpl @Inject constructor(
    private val insumoDao: InsumoDao,
    private val campaniaInsumoDao: CampaniaInsumoDao
) : InsumoRepository {

    override fun getInsumosVinculadosACampania(campaniaId: Int): Flow<List<CampaniaInsumo>> {
        return campaniaInsumoDao.getInsumosUtilizadosEnCampania(campaniaId).map { relations ->
            relations.map { it.toDomain() }
        }
    }

    override fun getAllInsumosVinculados(): Flow<List<CampaniaInsumo>> {
        return campaniaInsumoDao.getAllInsumosUtilizados().map { relations ->
            relations.map { it.toDomain() }
        }
    }

    override fun getAllInsumos(): Flow<List<Insumo>> {
        return insumoDao.getCatalogoInsumos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getInsumoById(id: Int): Insumo? {
        return insumoDao.getInsumoById(id)?.toDomain()
    }

    override suspend fun insertInsumo(insumo: Insumo) {
        insumoDao.insertInsumo(insumo.toEntity())
    }

    override suspend fun updateInsumo(insumo: Insumo) {
        insumoDao.updateInsumo(insumo.toEntity())
    }

    override suspend fun deleteInsumo(insumo: Insumo) {
        insumoDao.deleteInsumo(insumo.toEntity())
    }
}
