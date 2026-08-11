package com.itec.donelio.domain.use_case

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidarInsumoUseCaseTest {

    private lateinit var validarInsumoUseCase: ValidarInsumoUseCase

    @Before
    fun setUp() {
        validarInsumoUseCase = ValidarInsumoUseCase()
    }

    @Test
    fun `categoria vacia devuelve error`() {
        // Given
        val nombre = "Semillas"
        val categoria = ""

        // When
        val resultado = validarInsumoUseCase(nombre, categoria)

        // Then
        assertFalse(resultado.esValido)
        assertEquals("La categoría es obligatoria", resultado.errorCategoria)
    }

    @Test
    fun `ambos campos validos devuelve sin errores`() {
        // Given
        val nombre = "Semillas"
        val categoria = "Insumo Agrícola"

        // When
        val resultado = validarInsumoUseCase(nombre, categoria)

        // Then
        assertTrue(resultado.esValido)
        assertNull(resultado.errorNombre)
        assertNull(resultado.errorCategoria)
    }
}
