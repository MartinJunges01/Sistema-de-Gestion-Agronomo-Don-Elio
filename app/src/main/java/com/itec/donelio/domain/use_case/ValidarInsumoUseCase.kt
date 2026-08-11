package com.itec.donelio.domain.use_case

import javax.inject.Inject

data class ResultadoValidacionInsumo(
    val esValido: Boolean,
    val errorNombre: String? = null,
    val errorCategoria: String? = null
)

class ValidarInsumoUseCase @Inject constructor() {
    operator fun invoke(nombre: String, categoria: String): ResultadoValidacionInsumo {
        val errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
        val errorCategoria = if (categoria.isBlank()) "La categoría es obligatoria" else null
        
        return ResultadoValidacionInsumo(
            esValido = errorNombre == null && errorCategoria == null,
            errorNombre = errorNombre,
            errorCategoria = errorCategoria
        )
    }
}
