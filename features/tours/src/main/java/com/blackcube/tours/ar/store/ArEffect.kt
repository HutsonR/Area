package com.blackcube.tours.ar.store

sealed interface ArEffect {
    data object NavigateToBack : ArEffect
    data class NavigateWithId(val id: String) : ArEffect
}