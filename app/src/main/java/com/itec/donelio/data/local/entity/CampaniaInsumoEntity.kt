package com.itec.donelio.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad de vinculación entre una Campaña y un Insumo del catálogo.
 * Permite múltiples registros del mismo insumo en la misma campaña,
 * manteniendo historial real de cada aplicación con su fecha automática.
 */
@Entity(
    tableName = "campania_insumo",
    foreignKeys = [
        // Relación con la Campaña
        ForeignKey(
            entity = CampaniaEntity::class,
            parentColumns = ["id_campania"],
            childColumns = ["id_campania"],
            onDelete = ForeignKey.CASCADE // Si borras la campaña, se borran sus registros de insumos
        ),
        // Relación con el Catálogo de Insumos
        ForeignKey(
            entity = InsumoEntity::class,
            parentColumns = ["id_insumo"],
            childColumns = ["id_insumo"],
            onDelete = ForeignKey.CASCADE // Si borras el insumo del catálogo, se borra de las campañas
        )
    ],
    // Índices simples para las FK (rendimiento de Room).
    // El índice único compuesto fue eliminado para permitir múltiples aplicaciones del mismo insumo.
    indices = [
        Index(value = ["id_campania"]),
        Index(value = ["id_insumo"])
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
    val precio: Double,

    /** Timestamp en milisegundos de cuando se registró esta aplicación. Se asigna automáticamente. */
    @ColumnInfo(name = "fecha_aplicacion")
    val fechaAplicacion: Long = 0L
)