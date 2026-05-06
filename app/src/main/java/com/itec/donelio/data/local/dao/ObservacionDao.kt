package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.ObservacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservacion(observacion: ObservacionEntity): Long

    @Update
    suspend fun updateObservacion(observacion: ObservacionEntity): Int

    @Delete
    suspend fun deleteObservacion(observacion: ObservacionEntity): Int

    // Filtra las observaciones para mostrar solo las que pertenecen a la campaña actual
    @Query("SELECT * FROM observaciones WHERE id_campania = :campaniaId ORDER BY id_observacion DESC")
    fun getObservacionesPorCampania(campaniaId: Int): Flow<List<ObservacionEntity>>
}