package com.itec.donelio.core.utils

import java.util.Calendar

/**
 * Retorna el timestamp correspondiente a las 00:00:00 del dia provisto.
 * @param currentMillis Tiempo actual (inyectable para testing determinista).
 */
fun getStartOfDay(currentMillis: Long = System.currentTimeMillis()): Long {
    return Calendar.getInstance().apply {
        timeInMillis = currentMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
