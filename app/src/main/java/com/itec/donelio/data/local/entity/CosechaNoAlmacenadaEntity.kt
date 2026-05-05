package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cosechas_no_almacenadas",
    foreignKeys = [
        ForeignKey(
            entity = CosechaEntity::class,
            parentColumns = ["id_cosecha"],
            childColumns = ["id_cosecha"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_cosecha")]
)
data class CosechaNoAlmacenadaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_cosecha_no_alm: Int = 0,
    val tipo: String,
    val precio: Double,
    val id_cosecha: Int
)