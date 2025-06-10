package com.jahi.pipelinetest.di

import com.jahi.pipelinetest.data.repository.EventRepositoryImpl
import com.jahi.pipelinetest.data.repository.TaskRepositoryImpl
import com.jahi.pipelinetest.domain.repository.EventRepositoryInterface
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
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
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepositoryInterface
}