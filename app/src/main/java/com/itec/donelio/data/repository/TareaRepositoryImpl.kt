package com.itec.donelio.data.repository

import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.data.mapper.toEntity
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TareaRepositoryImpl @Inject constructor(
    private val tareaDao: TareaDao
) : TareaRepository {

    override fun getTareasByCampania(idCampania: Int): Flow<List<Tarea>> {
        return tareaDao.getTareasPorCampania(idCampania).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllTareas(): Flow<List<Tarea>> {
        return tareaDao.getAllTareas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTareasPendientesGlobales(limite: Int, fechaLimite: Long): Flow<List<Tarea>> {
        return tareaDao.getTareasPendientesGlobales(limite, fechaLimite).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTareasByCampaniaAndFecha(idCampania: Int, fecha: Long): Flow<List<Tarea>> {
        return tareaDao.getTareasPorCampaniaYFecha(idCampania, fecha).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTareaById(id: Int): Tarea? {
        return tareaDao.getTareaById(id)?.toDomain()
    }

    override suspend fun insertTarea(tarea: Tarea): Long {
        return tareaDao.insertTarea(tarea.toEntity())
    }

    override suspend fun updateTarea(tarea: Tarea) {
        tareaDao.updateTarea(tarea.toEntity())
    }

    override suspend fun deleteTarea(tarea: Tarea) {
        tareaDao.deleteTarea(tarea.toEntity())
    }

    override suspend fun completeTarea(tareaId: Int, completada: Boolean) {
        tareaDao.getTareaById(tareaId)?.let { entity ->
            val updated = entity.copy(confirmar = completada)
            tareaDao.updateTarea(updated)
        }
    }
}
