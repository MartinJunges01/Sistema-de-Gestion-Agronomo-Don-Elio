package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "campanias",
    foreignKeys = [
        ForeignKey(
            entity = CultivoEntity::class,
            parentColumns = ["id_cultivo"],
            childColumns = ["id_cultivo"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("id_cultivo")]
)
data class CampaniaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_campania: Int = 0,
    val nombre: String,
    val hectareas: Double,
    val fecha: Long, // Guardaremos la fecha en milisegundos (Timestamp)
    val id_cultivo: Int,
    val estaActiva: Boolean = true
)