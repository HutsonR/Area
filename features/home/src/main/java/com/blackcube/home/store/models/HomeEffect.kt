package com.blackcube.home.store.models

sealed interface HomeEffect {
    data class NavigateToExcursion(val id: String) : HomeEffect
}
