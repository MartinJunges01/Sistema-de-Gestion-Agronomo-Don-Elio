package com.itec.donelio.domain.use_case

data class ResumenRendimiento(
    val capitalInvertido: Double,
    val ingresosBrutos: Double = 0.0,
    val balance: Double = 0.0,
    val totalCosechado: Double = 0.0,
    val costoPorTonelada: Double = 0.0
)
