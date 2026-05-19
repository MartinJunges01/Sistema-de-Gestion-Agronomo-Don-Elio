package com.itec.donelio.data.mapper

import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.CosechaEntity
import com.itec.donelio.data.local.entity.CosechaNoAlmacenadaEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import com.itec.donelio.data.local.entity.InsumoUtilizadoRelacion
import com.itec.donelio.data.local.entity.ObservacionEntity
import com.itec.donelio.data.local.entity.TareaEntity
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.domain.model.Tarea

fun CampaniaEntity.toDomain(): Campania {
    return Campania(
        id = id_campania,
        nombre = nombre,
        fechaInicio = fecha,
        estaActiva = estaActiva,
        cultivo = cultivo
    )
}

fun Campania.toEntity(): CampaniaEntity {
    return CampaniaEntity(
        id_campania = id,
        nombre = nombre,
        fecha = fechaInicio,
        cultivo = cultivo,
        estaActiva = estaActiva
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

fun ObservacionEntity.toDomain(): Observacion {
    return Observacion(
        id = id_observacion,
        texto = texto,
        imagenUri = imagenUri,
        idCampania = id_campania
    )
}

fun Observacion.toEntity(): ObservacionEntity {
    return ObservacionEntity(
        id_observacion = id,
        texto = texto,
        imagenUri = imagenUri,
        id_campania = idCampania
    )
}

fun CampaniaInsumoEntity.toDomain(): CampaniaInsumo {
    return CampaniaInsumo(
        id = idCampaniaInsumo,
        idCampania = idCampania,
        idInsumo = idInsumo,
        cantidad = cantidad,
        precio = precio
    )
}

fun CampaniaInsumo.toEntity(): CampaniaInsumoEntity {
    return CampaniaInsumoEntity(
        idCampaniaInsumo = id,
        idCampania = idCampania,
        idInsumo = idInsumo,
        cantidad = cantidad,
        precio = precio
    )
}

fun InsumoUtilizadoRelacion.toDomain(): CampaniaInsumo {
    return asignacion.toDomain()
}

fun CosechaNoAlmacenadaEntity.toDomain(): CosechaNoAlmacenada =
    CosechaNoAlmacenada(id = id_cosecha_no_alm, tipo = tipo, precio = precio, idCosecha = id_cosecha)

fun CosechaNoAlmacenada.toEntity(): CosechaNoAlmacenadaEntity =
    CosechaNoAlmacenadaEntity(id_cosecha_no_alm = id, tipo = tipo, precio = precio, id_cosecha = idCosecha)
