package com.itec.donelio.presentation.util

import java.util.Locale

object FormatUtils {
    // Usamos el locale de Argentina (es_AR) que utiliza el punto (.) para los miles y la coma (,) para los decimales.
    private val locale = Locale("es", "AR")

    /**
     * Formatea un valor monetario (ej: $ 1.000,50).
     */
    fun formatMoneda(valor: Double): String {
        return "$ " + "%,.2f".format(locale, valor)
    }

    /**
     * Formatea un valor decimal genérico (ej: 1.000,50).
     */
    fun formatDecimal(valor: Double): String {
        return "%,.2f".format(locale, valor)
    }

    /**
     * Formatea una cantidad para mostrarla sin decimales si es entera, o con decimales si tiene.
     */
    fun formatCantidad(cantidad: Double, sufijo: String = ""): String {
        val str = if (cantidad == cantidad.toLong().toDouble()) {
            "%,d".format(locale, cantidad.toLong())
        } else {
            "%,.2f".format(locale, cantidad)
        }
        return if (sufijo.isNotBlank()) "$str $sufijo" else str
    }
}
