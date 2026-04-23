package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_usuario: Int = 0,

    val nombre: String,

    // Almacenaremos la contraseña ya hasheada como String
    val contrasena: String,

    // Almacenamos el timestamp (milisegundos) para facilitar cálculos y ordenamiento
    val ultimo_acceso: Long = System.currentTimeMillis()
)