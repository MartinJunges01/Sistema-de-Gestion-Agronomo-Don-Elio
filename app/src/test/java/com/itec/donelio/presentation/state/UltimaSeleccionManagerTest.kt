package com.itec.donelio.presentation.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UltimaSeleccionManagerTest {

    private lateinit var manager: UltimaSeleccionManager

    @Before
    fun setUp() {
        manager = UltimaSeleccionManager()
    }

    @Test
    fun `manager inicia con campania nula`() = runTest {
        val seleccion = manager.campaniaIdSeleccionada.first()
        assertEquals(null, seleccion)
    }

    @Test
    fun `manager guarda y emite el id de campania seleccionado`() = runTest {
        manager.seleccionarCampania(42)
        val seleccion = manager.campaniaIdSeleccionada.first()
        assertEquals(42, seleccion)
    }
}
