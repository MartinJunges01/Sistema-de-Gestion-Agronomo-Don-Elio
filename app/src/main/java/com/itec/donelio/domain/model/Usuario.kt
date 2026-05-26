package com.itec.donelio.domain.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val nombreUsuario: String,
    val contrasena: String,
    val ultimoAcceso: Long
)
