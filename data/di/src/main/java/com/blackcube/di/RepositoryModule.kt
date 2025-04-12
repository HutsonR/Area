package com.blackcube.di

import com.blackcube.remote.repository.tours.TourRepository
import com.blackcube.remote.repository.tours.TourRepositoryImpl
import com.blackcube.remote.repository.tts.TtsRepository
import com.blackcube.remote.repository.tts.TtsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindTtsRepository(ttsRepositoryImpl: TtsRepositoryImpl): TtsRepository

    @Binds
    fun bindTourRepository(tourRepositoryImpl: TourRepositoryImpl): TourRepository

}