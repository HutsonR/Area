package com.blackcube.auth.register.store.models

data class RegisterState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isActionButtonActive: Boolean = false,
    val emailErrorRes: Int? = null,
    val nameErrorRes: Int? = null,
    val passwordErrorRes: Int? = null
)
