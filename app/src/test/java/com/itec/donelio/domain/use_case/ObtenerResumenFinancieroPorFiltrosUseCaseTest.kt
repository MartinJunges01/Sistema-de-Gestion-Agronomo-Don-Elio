package com.itec.donelio.domain.use_case

import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import com.itec.donelio.domain.repository.CosechaNoAlmacenadaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObtenerResumenFinancieroPorFiltrosUseCaseTest {

    private lateinit var campaniaInsumoRepository: CampaniaInsumoRepository
    private lateinit var cosechaRepository: CosechaRepository
    private lateinit var cosechaNoAlmacenadaRepository: CosechaNoAlmacenadaRepository
    private lateinit var useCase: ObtenerResumenFinancieroPorFiltrosUseCase

    @Before
    fun setUp() {
        campaniaInsumoRepository = mockk()
        cosechaRepository = mockk()
        cosechaNoAlmacenadaRepository = mockk()
        useCase = ObtenerResumenFinancieroPorFiltrosUseCase(
            campaniaInsumoRepository,
            cosechaRepository,
            cosechaNoAlmacenadaRepository
        )
    }

    @Test
    fun `invoke calcula correctamente capital invertido, ingresos y balance con filtros vacios`() = runTest {
        // Given
        val insumos = listOf(
            CampaniaInsumo(id = 1, idCampania = 1, idInsumo = 1, cantidad = 10.0, precio = 100.0, fechaAplicacion = 0L)
        )
        val cosechas = listOf(
            Cosecha(id = 1, idCampania = 1, fecha = 1000L, cantidad = 5.0, almacen = "")
        )
        val ventas = listOf(
            CosechaNoAlmacenada(id = 1, idCosecha = 1, tipo = "Venta", precio = 2000.0)
        )

        coEvery { campaniaInsumoRepository.getAllInsumosUtilizados() } returns flowOf(insumos)
        coEvery { cosechaRepository.getAllCosechas() } returns flowOf(cosechas)
        coEvery { cosechaNoAlmacenadaRepository.getAllNoAlmacenadas() } returns flowOf(ventas)

        // When
        val result = useCase(emptyList(), null).first()

        // Then
        assertEquals(1000.0, result?.capitalInvertido ?: 0.0, 0.01)
        assertEquals(2000.0, result?.ingresosBrutos ?: 0.0, 0.01)
        assertEquals(1000.0, result?.balance ?: 0.0, 0.01)
    }
}
