package com.blackcube.data

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

}