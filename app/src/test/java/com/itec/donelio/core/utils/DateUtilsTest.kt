package com.itec.donelio.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun getStartOfDay_returnsMidnightOfGivenTimestamp() {
        // Given: 15 de Octubre de 2025 a las 14:30:45
        val testCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.OCTOBER)
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 45)
            set(Calendar.MILLISECOND, 500)
        }
        val givenMillis = testCalendar.timeInMillis

        // Expected: 15 de Octubre de 2025 a las 00:00:00
        val expectedCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.OCTOBER)
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expectedMillis = expectedCalendar.timeInMillis

        // When
        val result = getStartOfDay(givenMillis)

        // Then
        assertEquals("El timestamp debe corresponder a la medianoche del dia provisto", expectedMillis, result)
    }
}
