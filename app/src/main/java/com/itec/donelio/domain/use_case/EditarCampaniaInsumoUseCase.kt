package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import javax.inject.Inject

class EditarCampaniaInsumoUseCase @Inject constructor(
    private val repository: CampaniaInsumoRepository
) {
    suspend operator fun invoke(campaniaInsumo: CampaniaInsumo) {
        require(campaniaInsumo.id > 0) { "El insumo a editar debe tener un ID válido" }
        require(campaniaInsumo.cantidad > 0) { "La cantidad debe ser mayor a 0" }
        require(campaniaInsumo.precio >= 0) { "El precio no puede ser negativo" }
        
        repository.actualizarInsumo(campaniaInsumo)
    }
}
