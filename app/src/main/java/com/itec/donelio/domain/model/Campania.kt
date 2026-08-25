package com.itec.donelio.domain.model

data class Campania(
    val id: Int,
    val nombre: String,
    val hectareas: Double,
    val fechaInicio: Long,
    val estaActiva: Boolean,
    val cultivo: String
)