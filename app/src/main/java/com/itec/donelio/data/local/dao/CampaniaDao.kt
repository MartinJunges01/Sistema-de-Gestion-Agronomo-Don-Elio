package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaConCultivoSchema
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaniaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampania(campania: CampaniaEntity): Long

    @Update
    suspend fun updateCampania(campania: CampaniaEntity): Int

    @Delete
    suspend fun deleteCampania(campania: CampaniaEntity): Int

    @Query("""
        SELECT c.id_campania, c.nombre, c.hectareas, c.fecha, c.id_cultivo, c.estaActiva, cu.nombre AS cultivoNombre 
        FROM campanias c 
        INNER JOIN cultivos cu ON c.id_cultivo = cu.id_cultivo 
        ORDER BY c.fecha DESC
    """)
    fun getCampanias(): Flow<List<CampaniaConCultivoSchema>>

    @Query("""
        SELECT c.id_campania, c.nombre, c.hectareas, c.fecha, c.id_cultivo, c.estaActiva, cu.nombre AS cultivoNombre 
        FROM campanias c 
        INNER JOIN cultivos cu ON c.id_cultivo = cu.id_cultivo 
        WHERE c.estaActiva = 1 
        ORDER BY c.fecha DESC
    """)
    fun getCampaniasActivas(): Flow<List<CampaniaConCultivoSchema>>

    @Query("""
        SELECT c.id_campania, c.nombre, c.hectareas, c.fecha, c.id_cultivo, c.estaActiva, cu.nombre AS cultivoNombre 
        FROM campanias c 
        INNER JOIN cultivos cu ON c.id_cultivo = cu.id_cultivo 
        WHERE c.estaActiva = 0 
        ORDER BY c.fecha DESC
    """)
    fun getCampaniasInactivas(): Flow<List<CampaniaConCultivoSchema>>

    @Query("""
        SELECT c.id_campania, c.nombre, c.hectareas, c.fecha, c.id_cultivo, c.estaActiva, cu.nombre AS cultivoNombre 
        FROM campanias c 
        INNER JOIN cultivos cu ON c.id_cultivo = cu.id_cultivo 
        WHERE c.id_campania = :id
    """)
    suspend fun getCampaniaById(id: Int): CampaniaConCultivoSchema?
}