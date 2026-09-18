package com.itec.donelio.domain.model

data class CampaniaInsumo(
    val id: Int,
    val idCampania: Int,
    val idInsumo: Int,
    val nombreInsumo: String = "",
    val iconoInsumo: String? = null,
    val insumoActivo: Boolean = true,
    val cantidad: Double,
    val precio: Double,
    /** Timestamp en milisegundos de cuando se registró esta aplicación. */
    val fechaAplicacion: Long = 0L
)
