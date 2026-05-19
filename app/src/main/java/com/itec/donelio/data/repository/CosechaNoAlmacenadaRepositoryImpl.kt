package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.CosechaNoAlmacenadaDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CosechaNoAlmacenadaRepositoryImpl @Inject constructor(
    private val dao: CosechaNoAlmacenadaDao
) : CosechaNoAlmacenadaRepository {

    override fun getNoAlmacenadasPorCampania(idCampania: Int): Flow<List<CosechaNoAlmacenada>> =
        dao.getNoAlmacenadasPorCampania(idCampania)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPorCosechaId(cosechaId: Int): CosechaNoAlmacenada? =
        dao.getPorCosechaId(cosechaId)?.toDomain()

    override suspend fun insert(cosechaNoAlmacenada: CosechaNoAlmacenada) {
        dao.insert(cosechaNoAlmacenada.toEntity())
    }

    override suspend fun delete(cosechaNoAlmacenada: CosechaNoAlmacenada) {
        dao.delete(cosechaNoAlmacenada.toEntity())
    }
}
