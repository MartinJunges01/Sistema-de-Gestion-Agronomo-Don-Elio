package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campanias")
data class CampaniaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_campania: Int = 0,
    val nombre: String,
    val fecha: Long, // Guardaremos la fecha en milisegundos (Timestamp)
    val cultivo: String
)