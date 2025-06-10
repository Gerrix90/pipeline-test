package com.jahi.pipelinetest.di

import android.content.Context
import androidx.room.Room
import com.jahi.pipelinetest.data.local.TimeFomoDatabase
import com.jahi.pipelinetest.data.local.dao.CustomEventDao
import com.jahi.pipelinetest.data.local.dao.TaskDao
import com.jahi.pipelinetest.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTimeFomoDatabase(@ApplicationContext context: Context): TimeFomoDatabase {
        return Room.databaseBuilder(
            context,
            TimeFomoDatabase::class.java,
            "time_fomo_database"
        ).build()
    }

    @Provides
    fun provideCustomEventDao(database: TimeFomoDatabase): CustomEventDao {
        return database.customEventDao()
    }

    @Provides
    fun provideTaskDao(database: TimeFomoDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext context: Context): Prefs {
        return Prefs(context)
    }
}