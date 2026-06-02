package com.itec.donelio.domain.use_case

import com.itec.donelio.data.local.dao.UsuarioDao
import com.itec.donelio.data.local.entity.UsuarioEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var usuarioDao: UsuarioDao
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        usuarioDao = mockk()
        loginUseCase = LoginUseCase(usuarioDao)
    }

    @Test
    fun `invoke with correct credentials returns mapped Usuario`() = runTest {
        // Given
        val nombreUsuario = "DonElio"
        val contrasenaPlana = "123456"
        val hash = hashContrasena(contrasenaPlana)
        
        val usuarioEntity = UsuarioEntity(
            id_usuario = 1,
            nombre = "Don Elio",
            nombreUsuario = nombreUsuario,
            contrasena = hash
        )

        coEvery { usuarioDao.getUsuarioByNombre(nombreUsuario) } returns usuarioEntity

        // When
        val result = loginUseCase(nombreUsuario, contrasenaPlana)

        // Then
        assertNotNull(result)
        assertEquals("Don Elio", result?.nombre)
        coVerify(exactly = 1) { usuarioDao.getUsuarioByNombre(nombreUsuario) }
    }

    @Test
    fun `invoke with incorrect password returns null`() = runTest {
        // Given
        val nombreUsuario = "DonElio"
        val contrasenaIncorrecta = "wrongpass"
        val hashReal = hashContrasena("123456")
        
        val usuarioEntity = UsuarioEntity(
            id_usuario = 1,
            nombre = "Don Elio",
            nombreUsuario = nombreUsuario,
            contrasena = hashReal
        )

        coEvery { usuarioDao.getUsuarioByNombre(nombreUsuario) } returns usuarioEntity

        // When
        val result = loginUseCase(nombreUsuario, contrasenaIncorrecta)

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke with inexistent user returns null`() = runTest {
        // Given
        val nombreUsuario = "Intruso"
        val contrasenaPlana = "123456"

        coEvery { usuarioDao.getUsuarioByNombre(nombreUsuario) } returns null

        // When
        val result = loginUseCase(nombreUsuario, contrasenaPlana)

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke with blank user throws exception`() = runTest {
        // Given
        val nombreUsuario = "   "
        val contrasenaPlana = "123456"

        // When / Then
        try {
            loginUseCase(nombreUsuario, contrasenaPlana)
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("El nombre de usuario no puede estar vacío", e.message)
        }
    }
}
