package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.InsumoUtilizadoRelacion
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaniaInsumoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun asignarInsumo(campaniaInsumo: CampaniaInsumoEntity): Long

    @Query("DELETE FROM campania_insumo WHERE id_campania_insumo = :id")
    suspend fun desvincularInsumo(id: Int)

    // @Transaction es clave aquí porque Room hará dos consultas por debajo:
    // 1. Buscar en campania_insumo
    // 2. Buscar los datos del insumo base en InsumoEntity
    @Transaction
    @Query("SELECT * FROM campania_insumo WHERE id_campania = :campaniaId")
    fun getInsumosUtilizadosEnCampania(campaniaId: Int): Flow<List<InsumoUtilizadoRelacion>>

    @Transaction
    @Query("SELECT * FROM campania_insumo")
    fun getAllInsumosUtilizados(): Flow<List<InsumoUtilizadoRelacion>>
}