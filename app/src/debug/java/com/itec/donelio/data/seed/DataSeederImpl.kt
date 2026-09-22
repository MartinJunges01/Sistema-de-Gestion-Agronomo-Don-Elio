package com.itec.donelio.data.seed

import com.itec.donelio.core.util.DataSeeder
import com.itec.donelio.data.local.dao.*
import com.itec.donelio.data.local.entity.*
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
    private val cultivoDao: CultivoDao,
    private val cosechaNoAlmacenadaDao: CosechaNoAlmacenadaDao
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
        // 0. Catálogo de Cultivos
        val idSoja = cultivoDao.insertCultivo(CultivoEntity(nombre = "Soja", activo = true))
        val idTrigo = cultivoDao.insertCultivo(CultivoEntity(nombre = "Trigo", activo = true))
        val idMaiz = cultivoDao.insertCultivo(CultivoEntity(nombre = "Maíz", activo = true))
        val idGirasol = cultivoDao.insertCultivo(CultivoEntity(nombre = "Girasol Alto Oleico", activo = true))
        val idSorgo = cultivoDao.insertCultivo(CultivoEntity(nombre = "Sorgo", activo = true))

        // 1. Catálogo de Insumos
        val idUrea = insumoDao.insertInsumo(InsumoEntity(nombre = "Urea 46%", categoria = "Fertilizante", icono = "🧪"))
        val idGlifosato = insumoDao.insertInsumo(InsumoEntity(nombre = "Glifosato 74%", categoria = "Herbicida", icono = "💧"))
        val idSemillaMaiz = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Maíz DK72-10", categoria = "Semilla", icono = "🌾"))
        val idAtrazina = insumoDao.insertInsumo(InsumoEntity(nombre = "Atrazina 90", categoria = "Herbicida", icono = "💧"))
        val idFosfato = insumoDao.insertInsumo(InsumoEntity(nombre = "Fosfato Monoamónico (MAP)", categoria = "Fertilizante", icono = "🧪"))
        val idSemillaSoja = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Soja Asgrow", categoria = "Semilla", icono = "🌱"))
        val id24D = insumoDao.insertInsumo(InsumoEntity(nombre = "2,4-D Sal Amina", categoria = "Herbicida", icono = "💧"))
        val idCoadyuvante = insumoDao.insertInsumo(InsumoEntity(nombre = "Coadyuvante Premium", categoria = "Adyuvante", icono = "💧"))
        val idFungicida = insumoDao.insertInsumo(InsumoEntity(nombre = "Fungicida Amistar", categoria = "Fungicida", icono = "🧪"))
        val idSemillaGirasol = insumoDao.insertInsumo(InsumoEntity(nombre = "Semilla Girasol Nidera", categoria = "Semilla", icono = "🌻"))

        // 2. Campañas (Para fotos: unas cuantas activas con mucha plata, una inactiva)
        val idCampaniaMaiz = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Maíz tardío Lote Sur", hectareas = 250.0, fecha = fechaRelativa(-150), id_cultivo = idMaiz.toInt(), estaActiva = true)
        )
        val idCampaniaSoja = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Soja 1ra Lote Don Elio", hectareas = 500.0, fecha = fechaRelativa(-90), id_cultivo = idSoja.toInt(), estaActiva = true)
        )
        val idCampaniaTrigo = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Trigo Invierno (Finalizada)", hectareas = 200.0, fecha = fechaRelativa(-300), id_cultivo = idTrigo.toInt(), estaActiva = false)
        )
        val idCampaniaGirasol = campaniaDao.insertCampania(
            CampaniaEntity(nombre = "Experimento Girasol", hectareas = 50.0, fecha = fechaRelativa(-30), id_cultivo = idGirasol.toInt(), estaActiva = true)
        )

        // 3. Tareas (Para probar el Dashboard: atrasadas, de hoy, futuras)
        // Atrasadas
        tareaDao.insertTarea(TareaEntity(nombre = "Comprar Urea", fecha = fechaRelativa(-5), hora = "08:00", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Control de malezas", fecha = fechaRelativa(-2), hora = "09:30", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        // Completadas
        tareaDao.insertTarea(TareaEntity(nombre = "Siembra Soja", fecha = fechaRelativa(-90), hora = "07:00", notificar = false, confirmar = true, id_campania = idCampaniaSoja.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Fumigación Temprana", fecha = fechaRelativa(-60), hora = "14:00", notificar = false, confirmar = true, id_campania = idCampaniaSoja.toInt()))
        // Para hoy y futuro cercano
        tareaDao.insertTarea(TareaEntity(nombre = "Aplicar 2,4-D", fecha = fechaRelativa(0), hora = "10:00", notificar = false, confirmar = false, id_campania = idCampaniaSoja.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Revisar trampas insectos", fecha = fechaRelativa(1), hora = "06:00", notificar = true, confirmar = false, id_campania = idCampaniaGirasol.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Fertilizar post-emergencia", fecha = fechaRelativa(3), hora = "08:30", notificar = true, confirmar = false, id_campania = idCampaniaMaiz.toInt()))
        tareaDao.insertTarea(TareaEntity(nombre = "Pulverización Fungicida", fecha = fechaRelativa(10), hora = "11:00", notificar = true, confirmar = false, id_campania = idCampaniaSoja.toInt()))

        // 4. Cosechas y Ventas (CosechaNoAlmacenada)
        // Maiz: Todo a silo
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 8500.0, fecha = fechaRelativa(-10), almacen = "Silo 1", id_campania = idCampaniaMaiz.toInt()))
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 4500.0, fecha = fechaRelativa(-8), almacen = "Silo 2", id_campania = idCampaniaMaiz.toInt()))
        
        // Soja: Mixto (Silo B y dos ventas muy grandes para tener buenos números)
        cosechaDao.insertCosecha(CosechaEntity(cantidad = 3200.0, fecha = fechaRelativa(-20), almacen = "Silo B", id_campania = idCampaniaSoja.toInt()))
        
        val idCosechaVentaSoja1 = cosechaDao.insertCosecha(CosechaEntity(cantidad = 12000.0, fecha = fechaRelativa(-15), almacen = "", id_campania = idCampaniaSoja.toInt()))
        cosechaNoAlmacenadaDao.insert(CosechaNoAlmacenadaEntity(tipo = "Venta Cooperativa", precio = 4500000.0, id_cosecha = idCosechaVentaSoja1.toInt()))
        
        val idCosechaVentaSoja2 = cosechaDao.insertCosecha(CosechaEntity(cantidad = 8000.0, fecha = fechaRelativa(-5), almacen = "", id_campania = idCampaniaSoja.toInt()))
        cosechaNoAlmacenadaDao.insert(CosechaNoAlmacenadaEntity(tipo = "Exportación", precio = 3200000.0, id_cosecha = idCosechaVentaSoja2.toInt()))

        // Trigo: Todo vendido hace mucho
        val idCosechaVentaTrigo = cosechaDao.insertCosecha(CosechaEntity(cantidad = 12500.0, fecha = fechaRelativa(-180), almacen = "", id_campania = idCampaniaTrigo.toInt()))
        cosechaNoAlmacenadaDao.insert(CosechaNoAlmacenadaEntity(tipo = "Venta Molino", precio = 1800000.0, id_cosecha = idCosechaVentaTrigo.toInt()))

        // 5. Asignación de Insumos (Gastos grandes)
        // Maiz
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idUrea.toInt(), cantidad = 2000.0, precio = 550.0, fechaAplicacion = fechaRelativa(-140)))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idSemillaMaiz.toInt(), cantidad = 150.0, precio = 18000.0, fechaAplicacion = fechaRelativa(-145)))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaMaiz.toInt(), idInsumo = idAtrazina.toInt(), cantidad = 150.0, precio = 850.0, fechaAplicacion = fechaRelativa(-130)))
        // Soja
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idSemillaSoja.toInt(), cantidad = 500.0, precio = 15000.0, fechaAplicacion = fechaRelativa(-85)))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idFosfato.toInt(), cantidad = 800.0, precio = 700.0, fechaAplicacion = fechaRelativa(-80)))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idGlifosato.toInt(), cantidad = 400.0, precio = 450.0, fechaAplicacion = fechaRelativa(-60)))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaSoja.toInt(), idInsumo = idFungicida.toInt(), cantidad = 150.0, precio = 1200.0, fechaAplicacion = fechaRelativa(-20)))
        // Girasol
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaGirasol.toInt(), idInsumo = idSemillaGirasol.toInt(), cantidad = 60.0, precio = 18500.0, fechaAplicacion = fechaRelativa(-25)))
        campaniaInsumoDao.asignarInsumo(CampaniaInsumoEntity(idCampania = idCampaniaGirasol.toInt(), idInsumo = idCoadyuvante.toInt(), cantidad = 50.0, precio = 120.0, fechaAplicacion = fechaRelativa(-10)))

        // 6. Observaciones pintorescas
        observacionDao.insertObservacion(ObservacionEntity(texto = "Apareció el chancho de Don Elio en el lote, pisoteó un par de surcos pero nada grave. Se reparó el alambrado.", imagenUri = null, id_campania = idCampaniaMaiz.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Lluvia espectacular de 45mm anoche. El lote norte perfila para rinde récord si no cae granizo.", imagenUri = null, id_campania = idCampaniaSoja.toInt()))
        observacionDao.insertObservacion(ObservacionEntity(texto = "Emergencia excelente. Se nota la calidad de la semilla de Girasol Nidera.", imagenUri = null, id_campania = idCampaniaGirasol.toInt()))
    }
}
