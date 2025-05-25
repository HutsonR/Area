package com.blackcube.remote.di

import com.blackcube.remote.api.auth.AuthApi
import com.blackcube.remote.api.encryption.EncryptionApi
import com.blackcube.remote.api.places.PlacesApi
import com.blackcube.remote.api.tours.ArApi
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
    fun provideTtsApi(retrofit: Retrofit): TtsApi {
        return retrofit.create(TtsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTourApi(retrofit: Retrofit): ToursApi {
        return retrofit.create(ToursApi::class.java)
    }

    @Provides
    @Singleton
    fun provideArApi(retrofit: Retrofit): ArApi {
        return retrofit.create(ArApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceApi(retrofit: Retrofit): PlacesApi {
        return retrofit.create(PlacesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEncryptionApi(retrofit: Retrofit): EncryptionApi {
        return retrofit.create(EncryptionApi::class.java)
    }
}