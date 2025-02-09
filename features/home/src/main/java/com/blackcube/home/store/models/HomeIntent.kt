package com.blackcube.home.store.models

sealed interface HomeIntent {
    data class OnExcursionClick(
        val id: String
    ) : HomeIntent
}