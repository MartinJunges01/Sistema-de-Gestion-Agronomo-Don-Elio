package com.itec.donelio.presentation.ui.screen.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests unitarios para la función de utilidad [formatearMoneda].
 *
 * Verifica que el balance financiero se muestre correctamente con
 * el signo negativo al frente (no al final como hace NumberFormat de locale es_AR),
 * y que se aplique el formato abreviado para valores grandes.
 *
 * Fix del Issue [#437].
 * Paradigma: Given-When-Then (Dado que... Cuando... Entonces...)
 */
class FormatearMonedaTest {

    // ── Valores positivos ──────────────────────────────────────────────

    /**
     * Dado un valor de 6.133.500,
     * Cuando se formatea,
     * Entonces debe devolver "$6,1M" (abreviado en millones).
     */
    @Test
    fun `formatea valor positivo en millones`() {
        assertEquals("\$6,1M", formatearMoneda(6_133_500.0))
    }

    /**
     * Dado un valor de 250.000,
     * Cuando se formatea,
     * Entonces debe devolver "$250K".
     */
    @Test
    fun `formatea valor positivo en miles`() {
        assertEquals("\$250K", formatearMoneda(250_000.0))
    }

    /**
     * Dado un valor de 1.500,
     * Cuando se formatea,
     * Entonces debe devolver "$1K" (abreviado en miles, cualquier valor >= 1000).
     */
    @Test
    fun `formatea valor positivo entre mil y un millon`() {
        assertEquals("\$1K", formatearMoneda(1_500.0))
    }

    /**
     * Dado un valor de 0,
     * Cuando se formatea,
     * Entonces debe devolver "$0".
     */
    @Test
    fun `formatea cero`() {
        assertEquals("\$0", formatearMoneda(0.0))
    }

    // ── Valores negativos ──────────────────────────────────────────────

    /**
     * Dado un balance negativo de -6.133.500 (caso del bug reportado),
     * Cuando se formatea,
     * Entonces debe devolver "-$6,1M" con el signo al frente (no al final).
     */
    @Test
    fun `formatea balance negativo en millones con signo al frente`() {
        assertEquals("-\$6,1M", formatearMoneda(-6_133_500.0))
    }

    /**
     * Dado un balance negativo de -250.000,
     * Cuando se formatea,
     * Entonces debe devolver "-$250K".
     */
    @Test
    fun `formatea balance negativo en miles`() {
        assertEquals("-\$250K", formatearMoneda(-250_000.0))
    }

    /**
     * Dado un balance negativo de -1.500,
     * Cuando se formatea,
     * Entonces debe devolver "-$1K" (abreviado en miles).
     */
    @Test
    fun `formatea balance negativo entre mil y un millon`() {
        assertEquals("-\$1K", formatearMoneda(-1_500.0))
    }

    // ── Casos límite ───────────────────────────────────────────────────

    /**
     * Dado exactamente 1.000.000,
     * Cuando se formatea,
     * Entonces debe devolver "$1,0M".
     */
    @Test
    fun `formatea exactamente un millon`() {
        assertEquals("\$1,0M", formatearMoneda(1_000_000.0))
    }

    /**
     * Dado exactamente 1.000,
     * Cuando se formatea,
     * Entonces debe devolver "$1K".
     */
    @Test
    fun `formatea exactamente mil`() {
        assertEquals("\$1K", formatearMoneda(1_000.0))
    }

    /**
     * Dado 999,
     * Cuando se formatea,
     * Entonces debe devolver "$999" (sin abreviar).
     */
    @Test
    fun `formatea valor bajo el umbral de miles`() {
        assertEquals("\$999", formatearMoneda(999.0))
    }
}
