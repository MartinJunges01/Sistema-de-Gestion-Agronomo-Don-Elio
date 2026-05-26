package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.CampaniaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaniaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampania(campania: CampaniaEntity): Long

    @Update
    suspend fun updateCampania(campania: CampaniaEntity): Int

    @Delete
    suspend fun deleteCampania(campania: CampaniaEntity): Int

    @Query("SELECT * FROM campanias ORDER BY fecha DESC")
    fun getCampanias(): Flow<List<CampaniaEntity>>

    @Query("SELECT * FROM campanias WHERE id_campania = :id")
    suspend fun getCampaniaById(id: Int): CampaniaEntity?
}