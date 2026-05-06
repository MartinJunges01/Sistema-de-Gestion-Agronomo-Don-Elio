package com.itec.donelio.data.mapper

import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.domain.model.Campania

fun CampaniaEntity.toDomain(): Campania {
    return Campania(
        id = id_campania,
        nombre = nombre,
        fechaInicio = fecha,
        estaActiva = true
    )
}

fun Campania.toEntity(): CampaniaEntity {
    return CampaniaEntity(
        id_campania = id,
        nombre = nombre,
        fecha = fechaInicio,
        cultivo = ""
    )
}
