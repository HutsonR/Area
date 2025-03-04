package com.blackcube.tours.route.store

sealed interface TourRouteEffect {
    data object NavigateToBack : TourRouteEffect

    data object ShowAlert : TourRouteEffect

    data class ShowMap(
        val request: String
    ) : TourRouteEffect

    data object ZoomPlus : TourRouteEffect

    data object ZoomMinus : TourRouteEffect

    data object SwitchArMode : TourRouteEffect
}
