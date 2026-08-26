package com.itec.donelio.data.local.entity

data class CampaniaConCultivoSchema(
    val id_campania: Int,
    val nombre: String,
    val hectareas: Double,
    val fecha: Long,
    val id_cultivo: Int,
    val estaActiva: Boolean,
    val cultivoNombre: String
)
