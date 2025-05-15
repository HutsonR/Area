package com.blackcube.auth.register.store.models

sealed interface RegisterEffect {
    data object Success : RegisterEffect

    data object NavigateToLogin : RegisterEffect

    data object ShowAlert : RegisterEffect
}