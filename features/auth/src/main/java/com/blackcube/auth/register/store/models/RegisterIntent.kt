package com.blackcube.auth.register.store.models

sealed interface RegisterIntent {
    data class OnEmailChange(val email: String): RegisterIntent
    data class OnNameChange(val name: String): RegisterIntent
    data class OnPasswordChange(val password: String): RegisterIntent
    data object OnLoginClick: RegisterIntent
    data object OnRegisterClick: RegisterIntent
}