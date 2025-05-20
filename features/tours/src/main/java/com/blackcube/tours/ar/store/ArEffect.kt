package com.blackcube.tours.ar.store

sealed interface ArEffect {
    data object NavigateToBack : ArEffect
}