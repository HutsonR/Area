package com.blackcube.tours.store.models

sealed interface TourEffect {
    data object NavigateToBack : TourEffect

    data object ShowAlert : TourEffect

    data class NavigateToStartTour(
        val id: String
    ) : TourEffect

    data class ShowMap(
        val request: String
    ) : TourEffect
}
