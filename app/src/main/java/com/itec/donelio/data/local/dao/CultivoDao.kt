package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.CultivoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCultivo(cultivo: CultivoEntity): Long

    @Update
    suspend fun updateCultivo(cultivo: CultivoEntity): Int

    @Query("UPDATE cultivos SET activo = 0 WHERE id_cultivo = :id")
    suspend fun softDeleteCultivo(id: Int)

    @Query("SELECT * FROM cultivos WHERE activo = 1 ORDER BY nombre ASC")
    fun getCultivosActivos(): Flow<List<CultivoEntity>>

    @Query("SELECT * FROM cultivos ORDER BY nombre ASC")
    fun getTodosLosCultivos(): Flow<List<CultivoEntity>>

    @Query("SELECT * FROM cultivos WHERE id_cultivo = :id")
    suspend fun getCultivoById(id: Int): CultivoEntity?
}
