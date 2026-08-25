package com.itec.donelio.domain.model

data class Cultivo(
    val id: Int,
    val nombre: String,
    val activo: Boolean = true
)
