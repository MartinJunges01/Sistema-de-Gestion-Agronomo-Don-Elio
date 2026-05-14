package com.itec.donelio.di

import com.itec.donelio.data.repository.CampaniaInsumoRepositoryImpl
import com.itec.donelio.data.repository.CampaniaRepositoryImpl
import com.itec.donelio.data.repository.CosechaRepositoryImpl
import com.itec.donelio.data.repository.InsumoRepositoryImpl
import com.itec.donelio.data.repository.ObservacionRepositoryImpl
import com.itec.donelio.data.repository.TareaRepositoryImpl
import com.itec.donelio.domain.repository.CampaniaInsumoRepository
import com.itec.donelio.domain.repository.CampaniaRepository
import com.itec.donelio.domain.repository.CosechaRepository
import com.itec.donelio.domain.repository.InsumoRepository
import com.itec.donelio.domain.repository.ObservacionRepository
import com.itec.donelio.domain.repository.TareaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCampaniaRepository(
        impl: CampaniaRepositoryImpl
    ): CampaniaRepository

    @Binds
    @Singleton
    abstract fun bindTareaRepository(
        impl: TareaRepositoryImpl
    ): TareaRepository

    @Binds
    @Singleton
    abstract fun bindCosechaRepository(
        impl: CosechaRepositoryImpl
    ): CosechaRepository

    @Binds
    @Singleton
    abstract fun bindInsumoRepository(
        impl: InsumoRepositoryImpl
    ): InsumoRepository

    @Binds
    @Singleton
    abstract fun bindCampaniaInsumoRepository(
        impl: CampaniaInsumoRepositoryImpl
    ): CampaniaInsumoRepository

    @Binds
    @Singleton
    abstract fun bindObservacionRepository(
        impl: ObservacionRepositoryImpl
    ): ObservacionRepository
}
