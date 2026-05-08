package com.itec.donelio.data.mapper

import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CosechaEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import com.itec.donelio.data.local.entity.TareaEntity
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.model.Tarea

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

fun TareaEntity.toDomain(): Tarea {
    return Tarea(
        id = id_tarea,
        nombre = nombre,
        fecha = fecha,
        hora = hora,
        notificar = notificar,
        confirmar = confirmar,
        idCampania = id_campania
    )
}

fun Tarea.toEntity(): TareaEntity {
    return TareaEntity(
        id_tarea = id,
        nombre = nombre,
        fecha = fecha,
        hora = hora,
        notificar = notificar,
        confirmar = confirmar,
        id_campania = idCampania
    )
}

fun CosechaEntity.toDomain(): Cosecha {
    return Cosecha(
        id = id_cosecha,
        cantidad = cantidad,
        fecha = fecha,
        unidad = unidad,
        almacen = almacen,
        idCampania = id_campania
    )
}

fun Cosecha.toEntity(): CosechaEntity {
    return CosechaEntity(
        id_cosecha = id,
        cantidad = cantidad,
        fecha = fecha,
        unidad = unidad,
        almacen = almacen,
        id_campania = idCampania
    )
}

fun InsumoEntity.toDomain(): Insumo {
    return Insumo(
        id = id_insumo,
        nombre = nombre,
        categoria = categoria,
        unidad = unidad
    )
}

fun Insumo.toEntity(): InsumoEntity {
    return InsumoEntity(
        id_insumo = id,
        nombre = nombre,
        categoria = categoria,
        unidad = unidad
    )
}
