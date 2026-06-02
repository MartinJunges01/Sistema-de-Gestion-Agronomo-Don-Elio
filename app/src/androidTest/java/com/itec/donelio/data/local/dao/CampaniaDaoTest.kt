package com.itec.donelio.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itec.donelio.data.local.DonElioDatabase
import com.itec.donelio.data.local.entity.CampaniaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaniaDaoTest {

    private lateinit var database: DonElioDatabase
    private lateinit var campaniaDao: CampaniaDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DonElioDatabase::class.java
        ).allowMainThreadQueries().build()
        campaniaDao = database.campaniaDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetCampania() = runTest {
        val campania = CampaniaEntity(
            nombre = "Soja 2026",
            cultivo = "Soja",
            fecha = 1680000000000L,
            estaActiva = true
        )

        val id = campaniaDao.insertCampania(campania)
        val retrieved = campaniaDao.getCampaniaById(id.toInt())
        
        assertNotNull(retrieved)
        assertEquals("Soja 2026", retrieved?.nombre)
    }

    @Test
    fun updateCampaniaStatus() = runTest {
        val campania = CampaniaEntity(
            nombre = "Trigo",
            cultivo = "Trigo",
            fecha = 1680000000000L,
            estaActiva = true
        )

        val id = campaniaDao.insertCampania(campania).toInt()
        
        // Simular update
        val campaniaActualizada = campania.copy(id_campania = id, estaActiva = false)
        campaniaDao.updateCampania(campaniaActualizada)

        val retrieved = campaniaDao.getCampaniaById(id)
        assertEquals(false, retrieved?.estaActiva)
    }

    @Test
    fun getAllCampanias_returnsFlow() = runTest {
        campaniaDao.insertCampania(CampaniaEntity(nombre = "C1", cultivo = "A", fecha = 1000L))
        campaniaDao.insertCampania(CampaniaEntity(nombre = "C2", cultivo = "B", fecha = 1000L))

        val lista = campaniaDao.getAllCampanias().first()
        assertEquals(2, lista.size)
    }
}
