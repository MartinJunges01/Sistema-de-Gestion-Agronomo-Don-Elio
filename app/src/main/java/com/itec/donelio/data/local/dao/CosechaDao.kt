package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.CosechaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface   CosechaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCosecha(cosecha: CosechaEntity): Long

    @Update
    suspend fun updateCosecha(cosecha: CosechaEntity): Int

    @Delete
    suspend fun deleteCosecha(cosecha: CosechaEntity): Int

    @Query("SELECT * FROM cosechas WHERE id_campania = :campaniaId ORDER BY fecha DESC")
    fun getCosechasPorCampania(campaniaId: Int): Flow<List<CosechaEntity>>

    @Query("SELECT * FROM cosechas WHERE id_cosecha = :id")
    suspend fun getCosechaById(id: Int): CosechaEntity?
}