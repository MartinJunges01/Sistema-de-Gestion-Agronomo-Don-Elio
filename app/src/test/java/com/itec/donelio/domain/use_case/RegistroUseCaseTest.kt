package com.itec.donelio.domain.use_case

import com.itec.donelio.data.local.dao.UsuarioDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class RegistroUseCaseTest {

    private lateinit var usuarioDao: UsuarioDao
    private lateinit var registroUseCase: RegistroUseCase

    @Before
    fun setUp() {
        usuarioDao = mockk()
        registroUseCase = RegistroUseCase(usuarioDao)
    }

    @Test
    fun `invoke with valid data calls insertUsuario`() = runTest {
        val nombre = "Martin Junges"
        val nombreUsuario = "mjunges"
        val contrasena = "12345"

        coEvery { usuarioDao.getUsuarioByNombre(nombreUsuario) } returns null
        coEvery { usuarioDao.insertUsuario(any()) } returns 1L

        registroUseCase(nombre, nombreUsuario, contrasena)

        coVerify(exactly = 1) { 
            usuarioDao.insertUsuario(withArg {
                assertEquals(nombre, it.nombre)
                assertEquals(nombreUsuario, it.nombreUsuario)
                assertEquals(hashContrasena(contrasena), it.contrasena)
            }) 
        }
    }

    @Test
    fun `invoke with existing username throws exception`() = runTest {
        val nombre = "Martin Junges"
        val nombreUsuario = "mjunges"
        val contrasena = "12345"

        coEvery { usuarioDao.getUsuarioByNombre(nombreUsuario) } returns mockk()

        try {
            registroUseCase(nombre, nombreUsuario, contrasena)
            fail("Expected exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("El nombre de usuario ya existe", e.message)
        }
    }
}
