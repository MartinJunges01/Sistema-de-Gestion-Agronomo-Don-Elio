package com.itec.donelio.data.seed

import com.itec.donelio.core.util.DataSeeder
import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.local.dao.CultivoDao
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.CosechaEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import com.itec.donelio.data.local.entity.ObservacionEntity
import com.itec.donelio.data.local.entity.TareaEntity
import com.itec.donelio.data.local.entity.CultivoEntity
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
    private val observacionDao: ObservacionDao,
    private val cultivoDao: CultivoDao
) : DataSeeder {

    private fun fechaRelativa(diasOffset: Int, horaOffset: Int = 12): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, diasOffset)
            set(Calendar.HOUR_OF_DAY, horaOffset)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    override suspend fun seedData() {
        // 0. Catálogo de Cultivos por defecto
        val idSoja = cultivoDao.insertCultivo(CultivoEntity(nombre = "Soja", activo = true))
        val idTrigo = cultivoDao.insertCultivo(CultivoEntity(nombre = "Trigo", activo = true))
        val idMaiz = cultivoDao.insertCultivo(CultivoEntity(nombre = "Maíz", activo = true))
        val idGirasol = cultivoDao.insertCultivo(CultivoEntity(nombre = "Girasol", activo = true))
        val idCebada = cultivoDao.insertCultivo(CultivoEntity(nombre = "Cebada", activo = true))
        val idSorgo = cultivoDao.insertCultivo(CultivoEntity(nombre = "Sorgo", activo = true))
        val idAlgodon = cultivoDao.insertCultivo(CultivoEntity(nombre = "Algodón", activo = true))

        // 1. Catálogo de Insumos (ampliado y realista)
        val idUrea = insumoDao.insertInsumo(InsumoEntity(nombre = "Urea 46%", categoria = "Fertilizante", icono = "🧪"))
        val idGlifosato = insumoDao.insertInsumo(InsumoEntity(nombre = "Glifosato 74%", categoria = "Herbicida", icono = "💧"))
        val idSemillaMaiz = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Maíz DK72-10", categoria = "Semilla", icono = "🌾"))
        val idAtrazina = insumoDao.insertInsumo(InsumoEntity(nombre = "Atrazina 90", categoria = "Herbicida", icono = "💧"))
        val idFosfato = insumoDao.insertInsumo(InsumoEntity(nombre = "Fosfato Monoamónico (MAP)", categoria = "Fertilizante", icono = "🧪"))
        val idSemillaSoja = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Soja Asgrow", categoria = "Semilla", icono = "🌱"))
        val id24D = insumoDao.insertInsumo(InsumoEntity(nombre = "2,4-D Sal Amina", categoria = "Herbicida", icono = "💧"))
        val idAceite = insumoDao.insertInsumo(InsumoEntity(nombre = "Aceite Vegetal Coadyuvante", categoria = "Adyuvante", icono = "💧"))

        // 2. Campañas (Diferentes estados y fechas)
        val idCampaniaMaiz = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Maíz tardío Lote Sur", hectareas = 250.0, fecha = fechaRelativa(-150), id_cultivo = idMaiz.toInt(), estaActiva = true)
        )
        val idCampaniaSoja = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Soja 1ra Lote Norte", hectareas = 180.0, fecha = fechaRelativa(-90), id_cultivo = idSoja.toInt(), estaActiva = true)
        )
        val idCampaniaTrigo = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Trigo Invierno (Finalizada)", hectareas = 200.0, fecha = fechaRelativa(-300), id_cultivo = idTrigo.toInt(), estaActiva = false)
        )
        val idCampaniaGirasol = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Girasol Lote Este", hectareas = 150.0, fecha = fechaRelativa(-30), id_cultivo = idGirasol.toInt(), estaActiva = true)
        )

        // 3. Tareas (Para probar el Dashboard: atrasadas, de hoy, futuras)
        // Atrasadas (Dashboard Issue 1)
        tareaDao.insertTarea(TareaEntity(nombre = "Comprar Urea", fecha = fechaRelativa(-5), hora = "08:00", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Control de malezas", fecha = fechaRelativa(-2), hora = "09:30", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        // Completadas
        tareaDao.insertTarea(TareaEntity(nombre = "Siembra Soja", fecha = fechaRelativa(-90), hora = "07:00", notificar = false, confirmar = true, id_campania = idCampaniaSoja.toInt()))
        // Para hoy y futuro cercano (Dashboard)
        tareaDao.insertTarea(TareaEntity(nombre = "Aplicar 2,4-D", fecha = fechaRelativa(0), hora = "10:00", notificar = false, confirmar = false, id_campania = idCampaniaSoja.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Revisar trampas insectos", fecha = fechaRelativa(1), hora = "06:00", notificar = true, confirmar = false, id_campania = idCampaniaGirasol.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Fertilizar post-emergencia", fecha = fechaRelativa(3), hora = "08:30", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Pulverización Fungicida", fecha = fechaRelativa(10), hora = "11:00", notificar = true, confirmar = false, id_campania = idCampaniaSoja.toInt()))

        // 4. Cosechas (Con y sin almacén para testear gráfico #301, y con hectáreas preparatorias para #17)
        // Maiz: Todo a silo
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 8500.0, fecha = fechaRelativa(-10), almacen = "Silo 1", id_campania = idCampaniaMaiz.toInt()))
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 4500.0, fecha = fechaRelativa(-8), almacen = "Silo 2", id_campania = idCampaniaMaiz.toInt()))
        // Soja: Mixto (Almacenada vs Vendida)
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 3200.0, fecha = fechaRelativa(-20), almacen = "Silo B", id_campania = idCampaniaSoja.toInt()))
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 6000.0, fecha = fechaRelativa(-15), almacen = "", id_campania = idCampaniaSoja.toInt())) // Vendida directa
        // Trigo: Todo vendido
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 12500.0, fecha = fechaRelativa(-180), almacen = "", id_campania = idCampaniaTrigo.toInt()))
        
        // 5. Asignación de Insumos (Para probar comparador y reportes #302)
        // Maiz (Mucha Urea y semilla)
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idUrea.toInt(), cantidad = 2000.0, precio = 550.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idSemillaMaiz.toInt(), cantidad = 150.0, precio = 18000.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idAtrazina.toInt(), cantidad = 150.0, precio = 850.0))
        // Soja (MAP y Glifosato)
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idSemillaSoja.toInt(), cantidad = 120.0, precio = 15000.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idFosfato.toInt(), cantidad = 800.0, precio = 700.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idGlifosato.toInt(), cantidad = 200.0, precio = 450.0))
        // Trigo y Girasol (Costos menores)
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaTrigo.toInt(), idInsumo = idUrea.toInt(), cantidad = 1000.0, precio = 500.0))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaGirasol.toInt(), idInsumo = idAceite.toInt(), cantidad = 50.0, precio = 120.0))

        // 6. Observaciones
        observacionDao.insertObservacion(ObservacionEntity(texto = "Emergencia pareja. Se detecta algo de oruga cogollera en borduras.", imagenUri = null, id_campania = idCampaniaMaiz.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Lote muy enmalezado. Se aplicó doble dosis de glifo.", imagenUri = null, id_campania = idCampaniaSoja.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Rinde promedio histórico superado. Buena calidad de grano.", imagenUri = null, id_campania = idCampaniaTrigo.toInt()))
    }
}
