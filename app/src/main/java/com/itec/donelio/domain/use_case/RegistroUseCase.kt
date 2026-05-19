package com.itec.donelio.domain.use_case

import com.itec.donelio.data.local.dao.UsuarioDao
import com.itec.donelio.data.local.entity.UsuarioEntity
import javax.inject.Inject

class RegistroUseCase @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend operator fun invoke(nombre: String, nombreUsuario: String, contrasena: String) {
        if (nombre.isBlank()) throw IllegalArgumentException("El nombre completo no puede estar vacío")
        if (nombreUsuario.isBlank()) throw IllegalArgumentException("El nombre de usuario no puede estar vacío")
        if (contrasena.length < 4) throw IllegalArgumentException("La contraseña debe tener al menos 4 caracteres")

        val existente = usuarioDao.getUsuarioByNombre(nombreUsuario.trim())
        if (existente != null) throw IllegalArgumentException("El nombre de usuario ya existe")

        val hash = hashContrasena(contrasena)
        val entity = UsuarioEntity(
            nombre = nombre.trim(),
            nombreUsuario = nombreUsuario.trim(),
            contrasena = hash
        )
        usuarioDao.insertUsuario(entity)
    }
}
