package com.blackcube.tours.intro.store.models

sealed interface TourIntroEffect {
    data object NavigateToBack : TourIntroEffect

    data object ShowAlert : TourIntroEffect

    data class NavigateToStartTour(
        val id: String
    ) : TourIntroEffect

    data class ShowMap(
        val request: String
    ) : TourIntroEffect
}
