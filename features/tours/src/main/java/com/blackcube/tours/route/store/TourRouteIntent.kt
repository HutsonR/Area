package com.blackcube.tours.route.store

import com.blackcube.tours.common.models.HistoryModel

sealed interface TourRouteIntent {
    data class OnHistoryItemClick(
        val item: HistoryModel
    ) : TourRouteIntent

    data object OnBackClick: TourRouteIntent

    data object OnShowMapClick : TourRouteIntent

    data object OnZoomPlusClick: TourRouteIntent

    data object OnZoomMinusClick: TourRouteIntent

    data object ShowAlert : TourRouteIntent

    data object OnArClick: TourRouteIntent
}