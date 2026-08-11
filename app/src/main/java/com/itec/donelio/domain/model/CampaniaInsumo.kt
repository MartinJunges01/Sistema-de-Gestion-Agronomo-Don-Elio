package com.itec.donelio.domain.model

data class CampaniaInsumo(
    val id: Int,
    val idCampania: Int,
    val idInsumo: Int,
    val nombreInsumo: String = "",
    val insumoActivo: Boolean = true,
    val cantidad: Double,
    val precio: Double
)
