package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.util.ValidationResult
import javax.inject.Inject

class ValidarDatosCosechaUseCase @Inject constructor() {
    operator fun invoke(
        cantidad: Double?,
        fecha: Long?,
        isAlmacenada: Boolean,
        almacen: String
    ): ValidationResult {
        if (cantidad == null || cantidad <= 0) {
            return ValidationResult.Error("La cantidad debe ser mayor a 0.")
        }
        if (fecha == null) {
            return ValidationResult.Error("La fecha es obligatoria.")
        }
        
        if (isAlmacenada && almacen.isBlank()) {
            return ValidationResult.Error("El nombre del almacén o silo es obligatorio.")
        }

        return ValidationResult.Success
    }
}
