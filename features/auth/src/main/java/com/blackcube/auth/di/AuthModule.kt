package com.blackcube.auth.di

import com.blackcube.auth.domain.AuthUseCase
import com.blackcube.auth.domain.AuthUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AuthModule {

    @Binds
    fun bindAuthUseCase(authUseCaseImpl: AuthUseCaseImpl): AuthUseCase

}