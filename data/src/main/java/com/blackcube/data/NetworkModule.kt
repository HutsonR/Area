package com.blackcube.data

import com.blackcube.remote.api.tts.TtsApi
import com.blackcube.remote.provider.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataNetworkModule {

    @Provides
    @Singleton
    fun getTtsApi(provider: RetrofitProvider): TtsApi =
        provider.createTtsApi()

}