package com.itec.donelio.domain.model

data class Cosecha(
    val id: Int,
    val cantidad: Double,
    val fecha: Long,
    val almacen: String,
    val idCampania: Int
)
