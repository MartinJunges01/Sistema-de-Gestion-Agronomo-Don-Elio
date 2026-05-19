package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itec.donelio.data.local.entity.CosechaNoAlmacenadaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CosechaNoAlmacenadaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CosechaNoAlmacenadaEntity): Long

    @Query("SELECT * FROM cosechas_no_almacenadas WHERE id_cosecha = :cosechaId")
    suspend fun getPorCosechaId(cosechaId: Int): CosechaNoAlmacenadaEntity?

    @Query("SELECT * FROM cosechas_no_almacenadas WHERE id_cosecha IN " +
           "(SELECT id_cosecha FROM cosechas WHERE id_campania = :campaniaId AND almacen = '')")
    fun getNoAlmacenadasPorCampania(campaniaId: Int): Flow<List<CosechaNoAlmacenadaEntity>>

    @Delete
    suspend fun delete(entity: CosechaNoAlmacenadaEntity): Int
}
