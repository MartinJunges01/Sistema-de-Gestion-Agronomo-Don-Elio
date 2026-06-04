package com.itec.donelio.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itec.donelio.data.local.DonElioDatabase
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaniaInsumoDaoTest {

    private lateinit var database: DonElioDatabase
    private lateinit var campaniaDao: CampaniaDao
    private lateinit var insumoDao: InsumoDao
    private lateinit var campaniaInsumoDao: CampaniaInsumoDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DonElioDatabase::class.java
        ).allowMainThreadQueries().build()
        campaniaDao = database.campaniaDao
        insumoDao = database.insumoDao
        campaniaInsumoDao = database.campaniaInsumoDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun asignarInsumo_y_getInsumosUtilizadosEnCampania() = runTest {
        // 1. Crear dependencias
        val campaniaId = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Campania 1", cultivo = "Trigo", fecha = 1000L)
        ).toInt()

        val insumoId = insumoDao.insertInsumo(
            InsumoEntity(nombre = "Glifosato", categoria = "Herbicida", unidad = "L", activo = true)
        ).toInt()

        // 2. Asignar
        campaniaInsumoDao.asignarInsumo(
            CampaniaInsumoEntity(
                idCampania = campaniaId,
                idInsumo = insumoId,
                cantidad = 5.0,
                precio = 100.0
            )
        )

        // 3. Verificar
        val resultados = campaniaInsumoDao.getInsumosUtilizadosEnCampania(campaniaId).first()
        assertEquals(1, resultados.size)
        val relacion = resultados[0]
        assertEquals(5.0, relacion.asignacion.cantidad, 0.0)
        assertNotNull(relacion.insumoBase)
        assertEquals("Glifosato", relacion.insumoBase.nombre)
    }

    @Test
    fun desvincularInsumo_lo_elimina_de_la_campania() = runTest {
        // 1. Crear dependencias
        val campaniaId = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Campania 1", cultivo = "Trigo", fecha = 1000L)
        ).toInt()

        val insumoId = insumoDao.insertInsumo(
            InsumoEntity(nombre = "Glifosato", categoria = "Herbicida", unidad = "L", activo = true)
        ).toInt()

        // 2. Asignar
        val relacionId = campaniaInsumoDao.asignarInsumo(
            CampaniaInsumoEntity(
                idCampania = campaniaId,
                idInsumo = insumoId,
                cantidad = 5.0,
                precio = 100.0
            )
        ).toInt()

        // 3. Desvincular
        campaniaInsumoDao.desvincularInsumo(relacionId)

        // 4. Verificar
        val resultados = campaniaInsumoDao.getInsumosUtilizadosEnCampania(campaniaId).first()
        assertEquals(0, resultados.size)
    }
}
