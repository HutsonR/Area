package com.blackcube.tours.route.store

sealed interface TourRouteIntent {
    data class OnHistoryItemClick(
        val historyId: String
    ) : TourRouteIntent

    data class OnHistoryCompleteClick(
        val historyId: String
    ) : TourRouteIntent

    data object StartTour : TourRouteIntent

    data object StopTour : TourRouteIntent

    data object OnBackClick : TourRouteIntent

    data class OnMoveLocationClick(
        val lat: Double? = null,
        val lon: Double? = null
    ) : TourRouteIntent

    data object OnShowMapClick : TourRouteIntent

    data object ShowAlert : TourRouteIntent

    data object OnArClick : TourRouteIntent

    data class OnArObjectFound(
        val id: String
    ) : TourRouteIntent
}