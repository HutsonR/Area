package com.blackcube.tours.route.store

sealed interface TourRouteIntent {
    data class OnHistoryItemClick(
        val historyId: String
    ) : TourRouteIntent

    data object OnBackClick: TourRouteIntent

    data object OnShowMapClick : TourRouteIntent

    data object ShowAlert : TourRouteIntent

    data object OnArClick: TourRouteIntent
}