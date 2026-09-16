package com.itec.donelio.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itec.donelio.data.local.DonElioDatabase
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.InsumoEntity
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
class CampaniaInsumoDaoTest {

    private lateinit var database: DonElioDatabase
    private lateinit var campaniaDao: CampaniaDao
    private lateinit var insumoDao: InsumoDao
    private lateinit var campaniaInsumoDao: CampaniaInsumoDao
    private lateinit var cultivoDao: CultivoDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DonElioDatabase::class.java
        ).allowMainThreadQueries().build()
        campaniaDao = database.campaniaDao
        insumoDao = database.insumoDao
        campaniaInsumoDao = database.campaniaInsumoDao
        cultivoDao = database.cultivoDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun asignarInsumo_y_getInsumosUtilizadosEnCampania() = runTest {
        // 1. Crear dependencias
        val cultivoId = cultivoDao.insertCultivo(
            CultivoEntity(nombre = "Trigo", activo = true)
        ).toInt()

        val campaniaId = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Campania 1", id_cultivo = cultivoId, hectareas = 100.0, fecha = 1000L)
        ).toInt()

        val insumoId = insumoDao.insertInsumo(
            InsumoEntity(nombre = "Glifosato", categoria = "Herbicida", activo = true)
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
        val cultivoId = cultivoDao.insertCultivo(
            CultivoEntity(nombre = "Trigo", activo = true)
        ).toInt()

        val campaniaId = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Campania 1", id_cultivo = cultivoId, hectareas = 100.0, fecha = 1000L)
        ).toInt()

        val insumoId = insumoDao.insertInsumo(
            InsumoEntity(nombre = "Glifosato", categoria = "Herbicida", activo = true)
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

    /**
     * [#455] Dado: mismo insumo vinculado dos veces a la misma campaña
     * Cuando: se insertan dos CampaniaInsumoEntity con el mismo (idCampania, idInsumo)
     * Entonces: ambos registros persisten con IDs distintos
     */
    @Test
    fun asignarMismoInsumo_dosVeces_creaRegistrosSeparados() = runTest {
        // 1. Crear dependencias
        val cultivoId = cultivoDao.insertCultivo(
            CultivoEntity(nombre = "Soja", activo = true)
        ).toInt()

        val campaniaId = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Campaña Multi", id_cultivo = cultivoId, hectareas = 50.0, fecha = 2000L)
        ).toInt()

        val insumoId = insumoDao.insertInsumo(
            InsumoEntity(nombre = "Glifosato", categoria = "Herbicida", activo = true)
        ).toInt()

        // 2. Asignar el mismo insumo dos veces
        val id1 = campaniaInsumoDao.asignarInsumo(
            CampaniaInsumoEntity(idCampania = campaniaId, idInsumo = insumoId, cantidad = 5.0, precio = 100.0, fechaAplicacion = 1000L)
        ).toInt()

        val id2 = campaniaInsumoDao.asignarInsumo(
            CampaniaInsumoEntity(idCampania = campaniaId, idInsumo = insumoId, cantidad = 3.0, precio = 120.0, fechaAplicacion = 2000L)
        ).toInt()

        // 3. Verificar que hay dos registros distintos
        val resultados = campaniaInsumoDao.getInsumosUtilizadosEnCampania(campaniaId).first()
        assertEquals("Deben existir dos registros del mismo insumo", 2, resultados.size)
        assert(id1 != id2) { "Los IDs deben ser diferentes" }

        val cantidades = resultados.map { it.asignacion.cantidad }.sorted()
        assertEquals(3.0, cantidades[0], 0.0)
        assertEquals(5.0, cantidades[1], 0.0)
    }

    /**
     * [#455] La fecha de aplicación se persiste correctamente en la base de datos.
     */
    @Test
    fun asignarInsumo_persisteFechaAplicacion() = runTest {
        // 1. Crear dependencias
        val cultivoId = cultivoDao.insertCultivo(
            CultivoEntity(nombre = "Maíz", activo = true)
        ).toInt()

        val campaniaId = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Campaña Fecha", id_cultivo = cultivoId, hectareas = 80.0, fecha = 3000L)
        ).toInt()

        val insumoId = insumoDao.insertInsumo(
            InsumoEntity(nombre = "Herbicida X", categoria = "Herbicida", activo = true)
        ).toInt()

        val fechaEsperada = System.currentTimeMillis()

        // 2. Asignar con fecha
        campaniaInsumoDao.asignarInsumo(
            CampaniaInsumoEntity(idCampania = campaniaId, idInsumo = insumoId, cantidad = 2.0, precio = 80.0, fechaAplicacion = fechaEsperada)
        )

        // 3. Verificar que la fecha se guardó
        val resultados = campaniaInsumoDao.getInsumosUtilizadosEnCampania(campaniaId).first()
        assertEquals(1, resultados.size)
        assertEquals(fechaEsperada, resultados[0].asignacion.fechaAplicacion)
    }
}
