package com.itec.donelio.domain.model

data class Insumo(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val icono: String? = null,
    val activo: Boolean = true
)
