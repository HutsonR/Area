package com.blackcube.authorization.di

import com.blackcube.authorization.api.SessionManager
import com.blackcube.authorization.impl.SessionManagerImpl
import com.blackcube.authorization.storage.EncryptedDataStoreTokenStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    @Singleton
    fun bindTokenStorage(tokenStorage: EncryptedDataStoreTokenStorage): com.blackcube.authorization.api.TokenStorage

    @Binds
    @Singleton
    fun bindSessionManager(sessionManagerImpl: SessionManagerImpl): SessionManager

}