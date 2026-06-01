package com.itec.donelio.di

import android.content.Context
import com.itec.donelio.core.alarm.TaskReminderScheduler
import com.itec.donelio.core.alarm.WorkManagerTaskReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideTaskReminderScheduler(
        @ApplicationContext context: Context
    ): TaskReminderScheduler {
        return WorkManagerTaskReminderScheduler(context)
    }
}
