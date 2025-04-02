package com.blackcube.places.store.models

sealed interface PlaceIntroEffect {
    data object NavigateToBack : PlaceIntroEffect

    data object ShowAlert : PlaceIntroEffect

    data class ShowMap(
        val request: String
    ) : PlaceIntroEffect
}