package com.blackcube.auth.login.store.models

sealed interface LoginIntent {
    data class OnEmailChange(val email: String): LoginIntent
    data class OnPasswordChange(val password: String): LoginIntent
    data object OnLoginClick: LoginIntent
    data object OnRegisterClick: LoginIntent
}