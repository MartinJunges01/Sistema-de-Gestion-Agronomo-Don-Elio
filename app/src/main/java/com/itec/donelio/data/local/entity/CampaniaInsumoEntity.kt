package com.itec.donelio.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "campania_insumo",
    foreignKeys = [
        // Relación con la Campaña
        ForeignKey(
            entity = CampaniaEntity::class,
            parentColumns = ["id_campania"],
            childColumns = ["id_campania"],
            onDelete = ForeignKey.CASCADE // Si borras la campaña, se borran sus registros de insumos utilizados
        ),
        // Relación con el Catálogo de Insumos
        ForeignKey(
            entity = InsumoEntity::class,
            parentColumns = ["id_insumo"],
            childColumns = ["id_insumo"],
            onDelete = ForeignKey.CASCADE // Si borras el insumo del catálogo, se borra de las campañas
        )
    ],
    // Los índices son OBLIGATORIOS en Room para columnas que son Foreign Keys,
    // de lo contrario el compilador arrojará advertencias de rendimiento.
    indices = [
        Index(value = ["id_campania"]),
        Index(value = ["id_insumo"]),
        Index(value = ["id_campania", "id_insumo"], unique = true)
    ]
)
data class CampaniaInsumoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_campania_insumo")
    val idCampaniaInsumo: Int = 0,

    @ColumnInfo(name = "id_campania")
    val idCampania: Int,

    @ColumnInfo(name = "id_insumo")
    val idInsumo: Int,

    @ColumnInfo(name = "cantidad")
    val cantidad: Double,

    @ColumnInfo(name = "precio")
    val precio: Double
)