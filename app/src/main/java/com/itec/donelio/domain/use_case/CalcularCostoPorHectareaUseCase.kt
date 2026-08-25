package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import javax.inject.Inject

class CalcularCostoPorHectareaUseCase @Inject constructor() {
    
    operator fun invoke(campania: Campania?, insumos: List<CampaniaInsumo>): Double {
        if (campania == null || campania.hectareas <= 0.0) {
            return 0.0
        }
        
        if (insumos.isEmpty()) {
            return 0.0
        }
        
        val costoTotal = insumos.sumOf { it.cantidad * it.precio }
        return costoTotal / campania.hectareas
    }
}
