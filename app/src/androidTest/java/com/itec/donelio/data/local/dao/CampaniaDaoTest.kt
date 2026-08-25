package com.itec.donelio.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itec.donelio.data.local.DonElioDatabase
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CultivoEntity
import com.itec.donelio.data.local.dao.CultivoDao
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
    private lateinit var cultivoDao: CultivoDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DonElioDatabase::class.java
        ).allowMainThreadQueries().build()
        campaniaDao = database.campaniaDao
        cultivoDao = database.cultivoDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetCampania() = runTest {
        val cultivoId = cultivoDao.insertCultivo(
            CultivoEntity(nombre = "Soja", activo = true)
        ).toInt()

        val campania = CampaniaEntity(
            nombre = "Soja 2026",
            id_cultivo = cultivoId,
            hectareas = 100.0,
            fecha = 1680000000000L,
            estaActiva = true
        )

        val id = campaniaDao.insertCampania(campania)
        val retrieved = campaniaDao.getCampaniaById(id.toInt())
        
        assertNotNull(retrieved)
        assertEquals("Soja 2026", retrieved?.campania?.nombre)
        assertEquals("Soja", retrieved?.cultivoNombre)
    }

    @Test
    fun updateCampaniaStatus() = runTest {
        val cultivoId = cultivoDao.insertCultivo(
            CultivoEntity(nombre = "Trigo", activo = true)
        ).toInt()

        val campania = CampaniaEntity(
            nombre = "Trigo",
            id_cultivo = cultivoId,
            hectareas = 100.0,
            fecha = 1680000000000L,
            estaActiva = true
        )

        val id = campaniaDao.insertCampania(campania).toInt()
        
        // Simular update
        val campaniaActualizada = campania.copy(id_campania = id, estaActiva = false)
        campaniaDao.updateCampania(campaniaActualizada)

        val retrieved = campaniaDao.getCampaniaById(id)
        assertEquals(false, retrieved?.campania?.estaActiva)
        assertEquals("Trigo", retrieved?.cultivoNombre)
    }

    @Test
    fun getAllCampanias_returnsFlow() = runTest {
        val cultivoIdA = cultivoDao.insertCultivo(CultivoEntity(nombre = "A", activo = true)).toInt()
        val cultivoIdB = cultivoDao.insertCultivo(CultivoEntity(nombre = "B", activo = true)).toInt()

        campaniaDao.insertCampania(CampaniaEntity(nombre = "C1", id_cultivo = cultivoIdA, hectareas = 100.0, fecha = 1000L))
        campaniaDao.insertCampania(CampaniaEntity(nombre = "C2", id_cultivo = cultivoIdB, hectareas = 100.0, fecha = 1000L))

        val lista = campaniaDao.getCampanias().first()
        assertEquals(2, lista.size)
    }
}
