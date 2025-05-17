package com.blackcube.remote.repository.auth

import com.blackcube.remote.api.auth.AuthApi
import com.blackcube.remote.models.LoginApiModel
import com.blackcube.remote.models.RegistrationApiModel
import javax.inject.Inject

interface AuthRepository {
    /**
     * Метод для регистрации пользователя
     * @return токен пользователя
     * */
    suspend fun register(email: String, name: String, password: String): String
    /**
     * Метод для авторизации пользователя
     * @return токен пользователя
     * */
    suspend fun login(email: String, password: String): String
}

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun register(
        email: String,
        name: String,
        password: String
    ): String = authApi.register(RegistrationApiModel(email, name, password)).token

    override suspend fun login(
        email: String,
        password: String
    ): String = authApi.login(LoginApiModel(email, password)).token

}