package com.itec.donelio.domain.use_case

import javax.inject.Inject

data class ResultadoValidacionInsumo(
    val esValido: Boolean,
    val errorNombre: String? = null,
    val errorCategoria: String? = null
)

/**
 * Caso de uso para validar que el nombre y categoría de un insumo no estén vacíos.
 */
class ValidarInsumoUseCase @Inject constructor() {
    /**
     * @param nombre El nombre del insumo.
     * @param categoria La categoría del insumo.
     * @return ResultadoValidacionInsumo con el estado de validación.
     */
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
