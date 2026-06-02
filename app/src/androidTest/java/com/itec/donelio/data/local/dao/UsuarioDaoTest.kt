package com.itec.donelio.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itec.donelio.data.local.DonElioDatabase
import com.itec.donelio.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsuarioDaoTest {

    private lateinit var database: DonElioDatabase
    private lateinit var usuarioDao: UsuarioDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DonElioDatabase::class.java
        ).allowMainThreadQueries().build()
        usuarioDao = database.usuarioDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetUsuarioByNombre() = runTest {
        val usuario = UsuarioEntity(
            nombre = "Juan Perez",
            nombreUsuario = "jperez",
            contrasena = "hash123"
        )
        
        usuarioDao.insertUsuario(usuario)
        
        val retrieved = usuarioDao.getUsuarioByNombre("jperez")
        assertNotNull(retrieved)
        assertEquals("Juan Perez", retrieved?.nombre)
    }

    @Test
    fun getUsuarioByNombre_NotFoundReturnsNull() = runTest {
        val retrieved = usuarioDao.getUsuarioByNombre("noexiste")
        assertNull(retrieved)
    }

    @Test
    fun getAllUsuarios_ReturnsFlow() = runTest {
        usuarioDao.insertUsuario(UsuarioEntity(nombre = "A", nombreUsuario = "a", contrasena = "1"))
        usuarioDao.insertUsuario(UsuarioEntity(nombre = "B", nombreUsuario = "b", contrasena = "2"))
        
        val usuarios = usuarioDao.getAllUsuarios().first()
        assertEquals(2, usuarios.size)
    }
}
