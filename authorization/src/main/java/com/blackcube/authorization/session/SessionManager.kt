package com.blackcube.authorization.session

import kotlinx.coroutines.flow.StateFlow

/**
 * Управляет JWT-сессией: хранит и отдаёт токен, флаг логина.
 */
interface SessionManager {
    /** Текущий JWT или null */
    suspend fun getToken(): String?

    /** Сохраняет новый JWT и переводит isLoggedIn в true */
    suspend fun setToken(token: String)

    /** Удаляет JWT и переводит isLoggedIn в false */
    suspend fun clearToken()

    /** true, если есть сохранённый токен */
    val isLoggedIn: StateFlow<Boolean>
}