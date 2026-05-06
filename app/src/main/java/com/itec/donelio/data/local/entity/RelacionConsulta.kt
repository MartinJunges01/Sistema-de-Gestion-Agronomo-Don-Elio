package com.itec.donelio.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

// Esta clase no es una tabla, es la estructura que usará Room para devolverte los datos combinados
data class InsumoUtilizadoRelacion(
    @Embedded val asignacion: CampaniaInsumoEntity,

    @Relation(
        parentColumn = "id_insumo",
        entityColumn = "id_insumo"
    )
    val insumoBase: InsumoEntity
)