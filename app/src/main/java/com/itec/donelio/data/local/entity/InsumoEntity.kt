package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insumos")
data class InsumoEntity(
    @PrimaryKey(autoGenerate = true)
    val id_insumo: Int = 0,
    val nombre: String,
    val categoria: String,
    val unidad: String
)