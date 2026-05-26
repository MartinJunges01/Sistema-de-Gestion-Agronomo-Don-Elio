package com.itec.donelio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itec.donelio.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombreUsuario LIMIT 1")
    suspend fun getUsuarioByNombre(nombreUsuario: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>
}
