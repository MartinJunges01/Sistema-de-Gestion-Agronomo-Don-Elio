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

    @Query("UPDATE insumos SET activo = 0 WHERE id_insumo = :id")
    suspend fun softDeleteInsumo(id: Int)

    @Query("SELECT * FROM insumos WHERE activo = 1 ORDER BY nombre ASC")
    fun getCatalogoInsumos(): Flow<List<InsumoEntity>>

    @Query("SELECT * FROM insumos WHERE id_insumo = :id")
    suspend fun getInsumoById(id: Int): InsumoEntity?
}