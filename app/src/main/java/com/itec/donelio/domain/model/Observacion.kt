package com.itec.donelio.domain.model

data class Observacion(
    val id: Int,
    val texto: String,
    val imagenUri: String?,
    val idCampania: Int
)
