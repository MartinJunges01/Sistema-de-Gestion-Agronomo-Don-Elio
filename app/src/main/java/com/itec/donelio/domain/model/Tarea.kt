package com.itec.donelio.domain.model

data class Tarea(
    val id: Int,
    val nombre: String,
    val fecha: Long,
    val hora: String,
    val notificar: Boolean,
    val confirmar: Boolean,
    val idCampania: Int
)
