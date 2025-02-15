package com.blackcube.tours.store.models

import com.blackcube.tours.models.HistoryModel

sealed interface TourIntent {
    data class OnHistoryItemClick(
        val item: HistoryModel
    ) : TourIntent

    data object OnShowMapClick : TourIntent

    data object OnStartTourClick : TourIntent

    data object OnBackClick: TourIntent

    data object ShowAlert : TourIntent
}