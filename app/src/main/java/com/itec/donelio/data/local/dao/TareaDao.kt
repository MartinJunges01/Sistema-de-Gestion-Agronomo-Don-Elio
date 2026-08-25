package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itec.donelio.data.local.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: TareaEntity): Long

    @Update
    suspend fun updateTarea(tarea: TareaEntity): Int

    @Delete
    suspend fun deleteTarea(tarea: TareaEntity): Int

    @Query("SELECT * FROM tareas WHERE id_campania = :campaniaId ORDER BY fecha ASC, hora ASC")
    fun getTareasPorCampania(campaniaId: Int): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE confirmar = 0 AND fecha >= :fechaLimite ORDER BY fecha ASC, hora ASC LIMIT :limite")
    fun getTareasPendientesGlobales(limite: Int, fechaLimite: Long): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id_campania = :campaniaId AND fecha = :fecha ORDER BY hora ASC")
    fun getTareasPorCampaniaYFecha(campaniaId: Int, fecha: Long): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id_tarea = :id")
    suspend fun getTareaById(id: Int): TareaEntity?

    @Query("SELECT * FROM tareas ORDER BY fecha ASC, hora ASC")
    fun getAllTareas(): Flow<List<TareaEntity>>
}