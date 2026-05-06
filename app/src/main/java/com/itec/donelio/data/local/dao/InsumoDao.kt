package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.InsumoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsumoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsumo(insumo: InsumoEntity): Long

    @Update
    suspend fun updateInsumo(insumo: InsumoEntity): Int

    @Delete
    suspend fun deleteInsumo(insumo: InsumoEntity): Int

    @Query("SELECT * FROM insumos ORDER BY nombre ASC")
    fun getCatalogoInsumos(): Flow<List<InsumoEntity>>
}