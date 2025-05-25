package com.blackcube.remote.di

import com.blackcube.remote.repository.auth.AuthRepository
import com.blackcube.remote.repository.auth.AuthRepositoryImpl
import com.blackcube.remote.repository.places.PlaceRepository
import com.blackcube.remote.repository.places.PlaceRepositoryImpl
import com.blackcube.remote.repository.tours.ArRepository
import com.blackcube.remote.repository.tours.ArRepositoryImpl
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

    @Binds
    fun bindArRepository(arRepositoryImpl: ArRepositoryImpl): ArRepository

    @Binds
    fun bindPlaceRepository(placeRepositoryImpl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

}