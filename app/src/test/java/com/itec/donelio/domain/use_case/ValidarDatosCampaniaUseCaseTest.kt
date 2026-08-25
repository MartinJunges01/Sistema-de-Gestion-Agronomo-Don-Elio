package com.itec.donelio.domain.use_case

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class ValidarDatosCampaniaUseCaseTest {

    private lateinit var validarDatosCampaniaUseCase: ValidarDatosCampaniaUseCase

    @Before
    fun setUp() {
        validarDatosCampaniaUseCase = ValidarDatosCampaniaUseCase()
    }

    @Test
    fun `nombre vacio devuelve error`() {
        // Given
        val nombre = ""
        val hectareas = 100.0
        val cultivo = "Soja"
        val fechaInicio = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
        val isEditMode = false

        // When
        val resultado = validarDatosCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio, isEditMode)

        // Then
        assertFalse(resultado.esValido)
        assertEquals("El nombre es obligatorio", resultado.errorNombre)
    }
    
    @Test
    fun `hectareas nulas o negativas devuelve error`() {
        // Given
        val nombre = "Campaña 1"
        val hectareas = -5.0
        val cultivo = "Soja"
        val fechaInicio = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
        val isEditMode = false

        // When
        val resultado = validarDatosCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio, isEditMode)

        // Then
        assertFalse(resultado.esValido)
        assertEquals("Las hectáreas deben ser un valor mayor a cero", resultado.errorHectareas)
    }

    @Test
    fun `fecha pasada en creacion devuelve error`() {
        // Given
        val nombre = "Campaña 1"
        val hectareas = 100.0
        val cultivo = "Soja"
        val fechaInicio = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        val isEditMode = false

        // When
        val resultado = validarDatosCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio, isEditMode)

        // Then
        assertFalse(resultado.esValido)
        assertEquals("La fecha no puede ser anterior a hoy", resultado.errorFecha)
    }

    @Test
    fun `fecha pasada en edicion es valida`() {
        // Given
        val nombre = "Campaña 1"
        val hectareas = 100.0
        val cultivo = "Soja"
        val fechaInicio = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        val isEditMode = true

        // When
        val resultado = validarDatosCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio, isEditMode)

        // Then
        assertTrue(resultado.esValido)
        assertNull(resultado.errorFecha)
    }

    @Test
    fun `todos los campos validos devuelve sin errores`() {
        // Given
        val nombre = "Campaña 1"
        val hectareas = 100.0
        val cultivo = "Soja"
        val fechaInicio = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
        val isEditMode = false

        // When
        val resultado = validarDatosCampaniaUseCase(nombre, hectareas, cultivo, fechaInicio, isEditMode)

        // Then
        assertTrue(resultado.esValido)
        assertNull(resultado.errorNombre)
        assertNull(resultado.errorCultivo)
        assertNull(resultado.errorFecha)
        assertNull(resultado.errorHectareas)
    }
}
