package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CampaniaInsumoRepositoryImpl @Inject constructor(
    private val campaniaInsumoDao: CampaniaInsumoDao
) : CampaniaInsumoRepository {

    override fun getInsumosUtilizadosEnCampania(idCampania: Int): Flow<List<CampaniaInsumo>> {
        return campaniaInsumoDao.getInsumosUtilizadosEnCampania(idCampania).map { relaciones ->
            relaciones.map { it.toDomain() }
        }
    }

    override suspend fun asignarInsumo(campaniaInsumo: CampaniaInsumo) {
        campaniaInsumoDao.asignarInsumo(campaniaInsumo.toEntity())
    }

    override suspend fun desvincularInsumo(campaniaInsumo: CampaniaInsumo) {
        campaniaInsumoDao.desvincularInsumo(campaniaInsumo.toEntity())
    }
}
