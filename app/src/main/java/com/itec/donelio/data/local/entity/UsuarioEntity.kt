package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_usuario: Int = 0,
    val nombre: String,
    val nombreUsuario: String,
    val contrasena: String,
    val ultimo_acceso: Long = System.currentTimeMillis()
)