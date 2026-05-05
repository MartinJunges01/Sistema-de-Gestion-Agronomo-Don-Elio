package com.itec.donelio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = CampaniaEntity::class,
            parentColumns = ["id_campania"],
            childColumns = ["id_campania"],
            onDelete = ForeignKey.CASCADE // Si se borra la campaña, se borran sus tareas
        )
    ],
    indices = [Index("id_campania")] // Room recomienda indexar las claves foráneas para mayor velocidad
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_tarea: Int = 0,
    val nombre: String,
    val fecha: Long,
    val hora: String,
    val notificar: Boolean,
    val confirmar: Boolean,
    val id_campania: Int
)