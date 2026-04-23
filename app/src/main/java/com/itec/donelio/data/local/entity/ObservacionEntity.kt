package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "observaciones",
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
data class ObservacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id_observacion: Int = 0,
    val texto: String,
    val imagenUri: String?, // String nullable por si la observación no tiene foto
    val id_campania: Int
)