package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cultivos",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class CultivoEntity(
    @PrimaryKey(autoGenerate = true)
    val id_cultivo: Int = 0,
    val nombre: String,
    val activo: Boolean = true
)
