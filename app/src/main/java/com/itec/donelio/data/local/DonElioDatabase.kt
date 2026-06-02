package com.itec.donelio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.itec.donelio.data.local.dao.*
import com.itec.donelio.data.local.entity.*

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
// Aquí conectamos el Issue 3
@TypeConverters(Converters::class)
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