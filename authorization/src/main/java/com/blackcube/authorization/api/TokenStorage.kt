package com.blackcube.authorization.api

/**
 * Низкоуровневое хранилище токена (без логики шифрования/флоу).
 */
interface TokenStorage {
    suspend fun save(token: String)
    suspend fun get(): String?
    suspend fun clear()
}