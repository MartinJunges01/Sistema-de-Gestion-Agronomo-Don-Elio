package com.itec.donelio.domain.use_case

import com.itec.donelio.data.local.dao.UsuarioDao
import com.itec.donelio.data.mapper.toDomain
import com.itec.donelio.domain.model.Usuario
import java.security.MessageDigest
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend operator fun invoke(nombreUsuario: String, contrasena: String): Usuario? {
        if (nombreUsuario.isBlank()) throw IllegalArgumentException("El nombre de usuario no puede estar vacío")
        if (contrasena.isBlank()) throw IllegalArgumentException("La contraseña no puede estar vacía")

        val hash = hashContrasena(contrasena)
        val entity = usuarioDao.getUsuarioByNombre(nombreUsuario.trim())
        return if (entity != null && entity.contrasena == hash) {
            entity.toDomain()
        } else {
            null
        }
    }
}

fun hashContrasena(contrasena: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(contrasena.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
