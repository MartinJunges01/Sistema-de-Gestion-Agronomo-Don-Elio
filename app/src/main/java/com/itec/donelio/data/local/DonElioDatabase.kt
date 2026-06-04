package com.itec.donelio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.local.dao.CosechaNoAlmacenadaDao
import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.local.dao.UsuarioDao
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.CosechaEntity
import com.itec.donelio.data.local.entity.CosechaNoAlmacenadaEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import com.itec.donelio.data.local.entity.ObservacionEntity
import com.itec.donelio.data.local.entity.TareaEntity
import com.itec.donelio.data.local.entity.UsuarioEntity

// Aquí listamos TODAS las tablas (entities) que creaste en los Issues 1 y 2
@Database(
    entities = [
        CampaniaEntity::class,
        TareaEntity::class,
        CosechaEntity::class,
        CosechaNoAlmacenadaEntity::class,
        InsumoEntity::class,
        ObservacionEntity::class,
        CampaniaInsumoEntity::class,
        UsuarioEntity::class
    ],
    version = 4, // Subimos a 4 para eliminar el soft-delete de CampaniaInsumoEntity
    exportSchema = false // Por ahora lo dejamos en false para evitar warnings del compilador
)
abstract class DonElioDatabase : RoomDatabase() {

    // Aquí conectamos el Issue 4: Le decimos a Room qué DAOs existen
    abstract val campaniaDao: CampaniaDao
    abstract val tareaDao: TareaDao
    abstract val cosechaDao: CosechaDao
    abstract val insumoDao: InsumoDao
    abstract val campaniaInsumoDao: CampaniaInsumoDao
    abstract val observacionDao: ObservacionDao
    abstract val cosechaNoAlmacenadaDao: CosechaNoAlmacenadaDao
    abstract val usuarioDao: UsuarioDao

    // Nota: ¡No necesitas escribir el código de estas funciones!
    // Room generará todo el código real por detrás cuando compiles.
}