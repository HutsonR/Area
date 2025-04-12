package com.blackcube.di

import com.blackcube.remote.api.places.PlacesApi
import com.blackcube.remote.api.tours.ToursApi
import com.blackcube.remote.api.tts.TtsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Provides
    @Singleton
    fun provideTtsApi(@TtsRetrofit retrofit: Retrofit): TtsApi {
        return retrofit.create(TtsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTourApi(@MainRetrofit retrofit: Retrofit): ToursApi {
        return retrofit.create(ToursApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceApi(@MainRetrofit retrofit: Retrofit): PlacesApi {
        return retrofit.create(PlacesApi::class.java)
    }
}