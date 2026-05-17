package com.itec.donelio.di

import com.itec.donelio.core.util.DataSeeder
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SeedBindingModule {
    @BindsOptionalOf
    abstract fun optionalDataSeeder(): DataSeeder
}
