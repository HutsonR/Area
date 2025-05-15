package com.blackcube.auth.login.store.models

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isActionButtonActive: Boolean = false,
    val emailErrorRes: Int? = null,
    val passwordErrorRes: Int? = null
)
