package com.blackcube.auth.domain

import com.blackcube.authorization.api.SessionManager
import com.blackcube.remote.repository.auth.AuthRepository
import javax.inject.Inject

interface AuthUseCase {
    suspend fun register(email:String, name:String, password:String): Boolean
    suspend fun login(email:String, password:String): Boolean
}

class AuthUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
): AuthUseCase {

    override suspend fun register(email:String, name:String, password:String): Boolean {
        val token = authRepository.register(email, name, password)
        return if (!token.isNullOrBlank()) {
            sessionManager.setToken(token)
            true
        } else {
            false
        }
    }

    override suspend fun login(email:String, password:String): Boolean {
        val token = authRepository.login(email, password)
        return if (!token.isNullOrBlank()) {
            sessionManager.setToken(token)
            true
        } else {
            false
        }
    }
}