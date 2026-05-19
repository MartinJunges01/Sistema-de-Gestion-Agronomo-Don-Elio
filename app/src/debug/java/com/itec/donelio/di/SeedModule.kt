package com.itec.donelio.di

import com.itec.donelio.core.util.DataSeeder
import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.seed.DataSeederImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SeedModule {

    @Provides
    @Singleton
    fun provideDataSeeder(
        campaniaDao: CampaniaDao,
        tareaDao: TareaDao,
        cosechaDao: CosechaDao,
        insumoDao: InsumoDao,
        campaniaInsumoDao: CampaniaInsumoDao,
        observacionDao: ObservacionDao
    ): DataSeeder {
        return DataSeederImpl(
            campaniaDao = campaniaDao,
            tareaDao = tareaDao,
            cosechaDao = cosechaDao,
            insumoDao = insumoDao,
            campaniaInsumoDao = campaniaInsumoDao,
            observacionDao = observacionDao
        )
    }
}
