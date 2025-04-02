package com.blackcube.tours.intro.store.models

import com.blackcube.models.tours.HistoryModel

sealed interface TourIntroIntent {
    data class OnHistoryItemClick(
        val item: HistoryModel
    ) : TourIntroIntent

    data object OnShowMapClick : TourIntroIntent

    data object OnStartTourClick : TourIntroIntent

    data object OnBackClick: TourIntroIntent

    data object ShowAlert : TourIntroIntent
}