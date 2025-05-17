package com.blackcube.remote.repository.auth

import android.util.Base64
import com.blackcube.remote.api.auth.AuthApi
import com.blackcube.remote.api.encryption.EncryptionApi
import com.blackcube.remote.models.LoginApiModel
import com.blackcube.remote.models.RegistrationApiModel
import com.blackcube.remote.utils.EncryptionUtil
import javax.inject.Inject

interface AuthRepository {
    /**
     * Метод для регистрации пользователя
     * @return токен пользователя
     * */
    suspend fun register(email: String, name: String, password: String): String?
    /**
     * Метод для авторизации пользователя
     * @return токен пользователя
     * */
    suspend fun login(email: String, password: String): String?
}

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val encryptionApi: EncryptionApi,
    private val encryptionUtil: EncryptionUtil
) : AuthRepository {

    override suspend fun register(
        email: String,
        name: String,
        password: String
    ): String? {
        val publicKeyBase64 = encryptionApi.getPublicKey().publicKeyBase64
        val publicKey = Base64.decode(publicKeyBase64, Base64.NO_WRAP)

        val encryptedPassword = encryptionUtil.encryptRsa(password.toByteArray(), publicKey) ?: return null
        val registrationApiModel = RegistrationApiModel(email, name, encryptedPassword)
        return authApi.register(registrationApiModel).token
    }

    override suspend fun login(
        email: String,
        password: String
    ): String? {
        val publicKeyBase64 = encryptionApi.getPublicKey().publicKeyBase64
        val publicKey = Base64.decode(publicKeyBase64, Base64.NO_WRAP)

        val encryptedPassword = encryptionUtil.encryptRsa(password.toByteArray(), publicKey) ?: return null
        val registrationApiModel = LoginApiModel(email, encryptedPassword)
        return authApi.login(registrationApiModel).token
    }

}