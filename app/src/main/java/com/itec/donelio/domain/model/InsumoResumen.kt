package com.itec.donelio.domain.model

/**
 * Modelo de datos auxiliar utilizado para la exportación de reportes.
 * Representa el gasto agrupado por insumo en todas las campañas.
 */
data class InsumoResumen(
    val nombreInsumo: String,
    val cantidadTotal: Double,
    val costoTotal: Double
)
