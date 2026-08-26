package com.itec.donelio.domain.use_case

import java.util.Calendar
import javax.inject.Inject

data class ResultadoValidacionCampania(
    val esValido: Boolean,
    val errorNombre: String? = null,
    val errorHectareas: String? = null,
    val errorCultivo: String? = null,
    val errorFecha: String? = null
)

/**
 * Caso de uso para validar los datos de una campaña antes de crearla o editarla.
 * Asegura que el nombre y cultivo no estén en blanco, que las hectáreas sean mayores a 0,
 * y que la fecha no sea en el pasado si es creación.
 * @return ResultadoValidacionCampania con el estado general y mensajes de error por campo si los hay.
 */
class ValidarDatosCampaniaUseCase @Inject constructor() {
    /**
     * @param nombre El nombre de la campaña.
     * @param hectareas La cantidad de hectáreas (puede ser nulo si el input está vacío o es inválido).
     * @param cultivoId El ID del cultivo seleccionado.
     * @param fechaInicio La fecha de inicio en milisegundos.
     * @param isEditMode True si se está editando una campaña existente (permite fechas pasadas).
     */
    operator fun invoke(
        nombre: String, 
        hectareas: Double?,
        cultivoId: Int?, 
        fechaInicio: Long, 
        isEditMode: Boolean
    ): ResultadoValidacionCampania {
        val errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
        val errorHectareas = if (hectareas == null || hectareas <= 0) "Las hectáreas deben ser un valor mayor a cero" else null
        val errorCultivo = if (cultivoId == null || cultivoId <= 0) "El cultivo es obligatorio" else null
        
        var errorFecha: String? = null
        if (fechaInicio <= 0) {
            errorFecha = "Seleccione una fecha"
        } else if (!isEditMode) {
            val hoyMedianoche = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            if (fechaInicio < hoyMedianoche) {
                errorFecha = "La fecha no puede ser anterior a hoy"
            }
        }
        
        return ResultadoValidacionCampania(
            esValido = errorNombre == null && errorHectareas == null && errorCultivo == null && errorFecha == null,
            errorNombre = errorNombre,
            errorHectareas = errorHectareas,
            errorCultivo = errorCultivo,
            errorFecha = errorFecha
        )
    }
}
