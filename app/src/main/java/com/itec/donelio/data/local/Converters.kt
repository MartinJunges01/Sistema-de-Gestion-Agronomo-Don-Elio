package com.itec.donelio.data.local

import androidx.room.TypeConverter
import java.time.LocalTime
import java.util.Date

class Converters {

    // --- Conversores para java.util.Date (Fechas) ---

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        // Convierte el número Long (milisegundos) que viene de la base de datos a un objeto Date
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        // Convierte el objeto Date a un número Long (milisegundos) para guardarlo en SQLite
        return date?.time
    }

    // --- Conversores para java.time.LocalTime (Horas de las Tareas) ---
    // Nota: java.time.LocalTime es lo más recomendado en Kotlin moderno.

    @TypeConverter
    fun fromTime(value: String?): LocalTime? {
        // Convierte el texto guardado en SQLite (ej. "14:30") de vuelta a LocalTime
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        // Convierte el LocalTime a texto (String) para poder guardarlo
        return time?.toString()
    }
}