package com.itec.donelio.data.seed

import com.itec.donelio.core.util.DataSeeder
import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.CosechaEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import com.itec.donelio.data.local.entity.ObservacionEntity
import com.itec.donelio.data.local.entity.TareaEntity
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSeederImpl @Inject constructor(
    private val campaniaDao: CampaniaDao,
    private val tareaDao: TareaDao,
    private val cosechaDao: CosechaDao,
    private val insumoDao: InsumoDao,
    private val campaniaInsumoDao: CampaniaInsumoDao,
    private val observacionDao: ObservacionDao
) : DataSeeder {

    private fun fecha(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance().apply { set(year, month, day, 12, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis

    override suspend fun seedData() {
        val idUrea = insumoDao.insertInsumo(InsumoEntity(nombre = "Urea", categoria = "Fertilizante", unidad = "kg", icono = "🧪"))
        val idGlifosato = insumoDao.insertInsumo(InsumoEntity(nombre = "Glifosato", categoria = "Herbicida", unidad = "lt", icono = "💧"))
        val idSemillaMaiz = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Maíz", categoria = "Semilla", unidad = "bolsa", icono = "🌾"))
        val idAtrazina = insumoDao.insertInsumo(InsumoEntity(nombre = "Atrazina", categoria = "Herbicida", unidad = "lt", icono = "💧"))
        val idNPK = insumoDao.insertInsumo(InsumoEntity(nombre = "Fertilizante NPK", categoria = "Fertilizante", unidad = "kg", icono = "🧪"))
        val idSemillaSoja = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Soja", categoria = "Semilla", unidad = "bolsa", icono = "🌱"))
        val id24D = insumoDao.insertInsumo(InsumoEntity(nombre = "2,4-D Amína", categoria = "Herbicida", unidad = "lt", icono = "💧"))
        val idCoadyuvante = insumoDao.insertInsumo(InsumoEntity(nombre = "Coadyuvante", categoria = "Adyuvante", unidad = "lt", icono = "💧"))

        val idCampaniaMaiz = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Maíz tardío", fecha = fecha(2025, Calendar.NOVEMBER, 15), cultivo = "Maíz", estaActiva = true)
        )
        val idCampaniaSoja = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Soja 1ra", fecha = fecha(2025, Calendar.DECEMBER, 1), cultivo = "Soja", estaActiva = true)
        )
        val idCampaniaTrigo = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Trigo", fecha = fecha(2025, Calendar.JUNE, 20), cultivo = "Trigo", estaActiva = false)
        )
        val idCampaniaGirasol = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Girasol", fecha = fecha(2025, Calendar.OCTOBER, 1), cultivo = "Girasol", estaActiva = true)
        )

        tareaDao.insertTarea(TareaEntity(nombre = "Aplicar Urea", fecha = fecha(2025, Calendar.DECEMBER, 10), hora = "08:00", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Control de malezas", fecha = fecha(2025, Calendar.DECEMBER, 5), hora = "09:30", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Siembra Soja", fecha = fecha(2025, Calendar.DECEMBER, 2), hora = "07:00", notificar = true, confirmar = true, id_campania = idCampaniaSoja.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Aplicar 2,4-D", fecha = fecha(2025, Calendar.DECEMBER, 15), hora = "10:00", notificar = false, confirmar = false, id_campania = idCampaniaSoja.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Cosechar Trigo", fecha = fecha(2025, Calendar.DECEMBER, 20), hora = "06:00", notificar = true, confirmar = true, id_campania = idCampaniaTrigo.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Fertilizar post-cosecha", fecha = fecha(2025, Calendar.DECEMBER, 28), hora = "08:30", notificar = false, confirmar = true, id_campania = idCampaniaTrigo.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Riego Girasol", fecha = fecha(2026, Calendar.JANUARY, 5), hora = "07:00", notificar = true, confirmar = false, id_campania = idCampaniaGirasol.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Aplicar Coadyuvante", fecha = fecha(2026, Calendar.JANUARY, 10), hora = "11:00", notificar = true, confirmar = false, id_campania = idCampaniaGirasol.toInt()))

        val idCosechaMaiz = cosechaDao.insertCosecha(CosechaEntity(cantidad = 8500.0, fecha = fecha(2026, Calendar.MARCH, 15), unidad = "kg", almacen = "Silo 1", id_campania = idCampaniaMaiz.toInt()))
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 3200.0, fecha = fecha(2026, Calendar.APRIL, 10), unidad = "kg", almacen = "Silo 2", id_campania = idCampaniaSoja.toInt()))
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 1500.0, fecha = fecha(2026, Calendar.MARCH, 1), unidad = "kg", almacen = "Venta directa", id_campania = idCampaniaGirasol.toInt()))

        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idUrea.toInt(), cantidad = 200.0, precio = 45.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idAtrazina.toInt(), cantidad = 15.0, precio = 120.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idSemillaSoja.toInt(), cantidad = 10.0, precio = 350.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = id24D.toInt(), cantidad = 8.0, precio = 90.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaGirasol.toInt(), idInsumo = idCoadyuvante.toInt(), cantidad = 5.0, precio = 60.0))

        observacionDao.insertObservacion(ObservacionEntity(texto = "Maíz tardío con buena germinación, se espera rinde alto.", imagenUri = null, id_campania = idCampaniaMaiz.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Soja presenta manchas foliares leves. Monitorear.", imagenUri = null, id_campania = idCampaniaSoja.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Trigo cosechado con humedad dentro de parámetros.", imagenUri = null, id_campania = idCampaniaTrigo.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Girasol en etapa de floración. Buen estado general.", imagenUri = null, id_campania = idCampaniaGirasol.toInt()))
    }
}
