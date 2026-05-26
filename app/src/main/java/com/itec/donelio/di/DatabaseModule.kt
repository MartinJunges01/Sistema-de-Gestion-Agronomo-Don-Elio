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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Le dice a Hilt que esto vivirá mientras la app esté abierta
object DatabaseModule {

    // 1. Provee la Base de Datos completa
    @Provides
    @Singleton // Asegura que solo exista UNA instancia de la DB en toda la app
    fun provideDonElioDatabase(
        @ApplicationContext context: Context
    ): DonElioDatabase {
        return Room.databaseBuilder(
            context,
            DonElioDatabase::class.java,
            "don_elio_db" // Este es el nombre del archivo físico SQLite en el teléfono
        )
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