package com.blackcube.authorization.impl

import com.blackcube.authorization.session.SessionManager
import com.blackcube.authorization.session.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagerImpl @Inject constructor(
    private val tokenStorage: TokenStorage
) : SessionManager {
    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _isLoggedIn.value = tokenStorage.get().isNullOrBlank().not()
        }
    }

    override suspend fun getToken() = tokenStorage.get()

    override suspend fun setToken(token: String) {
        tokenStorage.save(token)
        _isLoggedIn.value = true
    }
    override suspend fun clearToken() {
        tokenStorage.clear()
        _isLoggedIn.value = false
    }
}