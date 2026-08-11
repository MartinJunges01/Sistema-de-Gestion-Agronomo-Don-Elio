package com.itec.donelio.domain.use_case

import java.util.Calendar
import javax.inject.Inject

data class ResultadoValidacionCampania(
    val esValido: Boolean,
    val errorNombre: String? = null,
    val errorCultivo: String? = null,
    val errorFecha: String? = null
)

/**
 * Caso de uso para validar los datos de una campaña antes de crearla o editarla.
 * Asegura que el nombre y cultivo no estén en blanco, y que la fecha no sea en el pasado si es creación.
 * @return ResultadoValidacionCampania con el estado general y mensajes de error por campo si los hay.
 */
class ValidarDatosCampaniaUseCase @Inject constructor() {
    /**
     * @param nombre El nombre de la campaña.
     * @param cultivo El tipo de cultivo.
     * @param fechaInicio La fecha de inicio en milisegundos.
     * @param isEditMode True si se está editando una campaña existente (permite fechas pasadas).
     */
    operator fun invoke(
        nombre: String, 
        cultivo: String, 
        fechaInicio: Long, 
        isEditMode: Boolean
    ): ResultadoValidacionCampania {
        val errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
        val errorCultivo = if (cultivo.isBlank()) "El cultivo es obligatorio" else null
        
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
            esValido = errorNombre == null && errorCultivo == null && errorFecha == null,
            errorNombre = errorNombre,
            errorCultivo = errorCultivo,
            errorFecha = errorFecha
        )
    }
}
