package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itec.donelio.data.local.entity.CosechaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CosechaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCosecha(cosecha: CosechaEntity): Long

    @Query("SELECT * FROM cosechas WHERE id_campania = :campaniaId ORDER BY fecha DESC")
    fun getCosechasPorCampania(campaniaId: Int): Flow<List<CosechaEntity>>
}