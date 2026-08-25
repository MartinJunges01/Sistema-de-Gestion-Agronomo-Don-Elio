package com.itec.donelio.di

import android.content.Context
import androidx.room.Room
import com.itec.donelio.data.local.DonElioDatabase
import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.local.dao.CosechaNoAlmacenadaDao
import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.local.dao.UsuarioDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Le dice a Hilt que esto vivirá mientras la app esté abierta
object DatabaseModule {

    val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Migración para la tabla 'insumos': quitamos 'unidad'
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `insumos_new` (`id_insumo` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `categoria` TEXT NOT NULL, `icono` TEXT, `activo` INTEGER NOT NULL)"
        )
        database.execSQL(
            "INSERT INTO `insumos_new` (`id_insumo`, `nombre`, `categoria`, `icono`, `activo`) SELECT `id_insumo`, `nombre`, `categoria`, `icono`, `activo` FROM `insumos`"
        )
        database.execSQL("DROP TABLE `insumos`")
        database.execSQL("ALTER TABLE `insumos_new` RENAME TO `insumos`")

        // Migración para la tabla 'cosechas': quitamos 'unidad'
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `cosechas_new` (`id_cosecha` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cantidad` REAL NOT NULL, `fecha` INTEGER NOT NULL, `almacen` TEXT NOT NULL, `id_campania` INTEGER NOT NULL, FOREIGN KEY(`id_campania`) REFERENCES `campanias`(`id_campania`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        database.execSQL(
            "INSERT INTO `cosechas_new` (`id_cosecha`, `cantidad`, `fecha`, `almacen`, `id_campania`) SELECT `id_cosecha`, `cantidad`, `fecha`, `almacen`, `id_campania` FROM `cosechas`"
        )
        database.execSQL("DROP TABLE `cosechas`")
        database.execSQL("ALTER TABLE `cosechas_new` RENAME TO `cosechas`")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_cosechas_id_campania` ON `cosechas` (`id_campania`)")
    }
}

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `campanias` ADD COLUMN `hectareas` REAL NOT NULL DEFAULT 0.0")
        }
    }

    // 1. Provee la Base de Datos completa
    @Provides
    @Singleton // Asegura que solo exista UNA instancia de la DB en toda la app
    fun provideDonElioDatabase(
        @ApplicationContext context: Context
    ): DonElioDatabase {
        return Room.databaseBuilder(
            context,
            DonElioDatabase::class.java,
            "don_elio_db" // Este es el nombre del archivo fisico SQLite en el telefono
        )
        .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
        .fallbackToDestructiveMigration() // Agregado para desarrollo: borra y recrea las tablas si cambia la version
        .build()
    }

    // 2. Provee los DAOs individuales
    // Hilt es inteligente: sabe que para darte un CampaniaDao, primero debe llamar a la función de arriba

    @Provides
    @Singleton
    fun provideCampaniaDao(db: DonElioDatabase): CampaniaDao {
            return db.campaniaDao
    }

    @Provides
    @Singleton
    fun provideTareaDao(db: DonElioDatabase): TareaDao {
        return db.tareaDao
    }

    @Provides
    @Singleton
    fun provideCosechaDao(db: DonElioDatabase): CosechaDao {
        return db.cosechaDao
    }

    @Provides
    @Singleton
    fun provideInsumoDao(db: DonElioDatabase): InsumoDao {
        return db.insumoDao
    }

    @Provides
    @Singleton
    fun provideCampaniaInsumoDao(db: DonElioDatabase): CampaniaInsumoDao {
        return db.campaniaInsumoDao
    }

    @Provides
    @Singleton
    fun provideObservacionDao(db: DonElioDatabase): ObservacionDao {
        return db.observacionDao
    }

    @Provides
    @Singleton
    fun provideCosechaNoAlmacenadaDao(db: DonElioDatabase): CosechaNoAlmacenadaDao {
        return db.cosechaNoAlmacenadaDao
    }

    @Provides
    @Singleton
    fun provideUsuarioDao(db: DonElioDatabase): UsuarioDao {
        return db.usuarioDao
    }
}