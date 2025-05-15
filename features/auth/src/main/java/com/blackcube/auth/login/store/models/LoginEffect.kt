package com.blackcube.auth.login.store.models

sealed interface LoginEffect {
    data object Success : LoginEffect

    data object NavigateToRegister : LoginEffect

    data object ShowAlert : LoginEffect
}