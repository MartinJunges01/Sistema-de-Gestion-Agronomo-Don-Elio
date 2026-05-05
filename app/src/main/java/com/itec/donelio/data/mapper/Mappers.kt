package com.itec.donelio.data.mapper

import com.tu.paquete.data.local.entities.CampaniaEntity
import com.tu.paquete.domain.model.Campania

// De Room a Dominio (Para leer datos)
fun CampaniaEntity.toDomain(): Campania {
    return Campania(
        id = this.id,
        nombre = this.nombre,
        fechaInicio = this.fechaInicio,
        estaActiva = this.estaActiva
    )
}

// De Dominio a Room (Para guardar datos)
fun Campania.toEntity(): CampaniaEntity {
    return CampaniaEntity(
        id = this.id,
        nombre = this.nombre,
        fechaInicio = this.fechaInicio,
        estaActiva = this.estaActiva
    )
}