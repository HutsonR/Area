package com.blackcube.remote.api.auth

import com.blackcube.models.auth.TokenModel
import com.blackcube.remote.models.LoginApiModel
import com.blackcube.remote.models.RegistrationApiModel
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(
        @Body registrationApiModel: RegistrationApiModel
    ): TokenModel

    @POST("auth/login")
    suspend fun login(
        @Body loginApiModel: LoginApiModel
    ): TokenModel
}