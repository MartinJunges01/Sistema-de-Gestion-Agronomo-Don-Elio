package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cosechas",
    foreignKeys = [
        ForeignKey(
            entity = CampaniaEntity::class,
            parentColumns = ["id_campania"],
            childColumns = ["id_campania"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_campania")]
)
data class CosechaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_cosecha: Int = 0,
    val cantidad: Double,
    val fecha: Long,
    val unidad: String,
    val almacen: String,
    val id_campania: Int
)