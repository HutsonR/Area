package com.blackcube.remote.models.auth

data class RegistrationApiModel(
    val email: String,
    val name: String,
    val password: String
)
