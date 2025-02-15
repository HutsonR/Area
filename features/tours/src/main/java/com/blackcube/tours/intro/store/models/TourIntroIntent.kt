package com.blackcube.tours.intro.store.models

import com.blackcube.tours.common.models.HistoryModel

sealed interface TourIntroIntent {
    data class OnHistoryItemClick(
        val item: HistoryModel
    ) : TourIntroIntent

    data object OnShowMapClick : TourIntroIntent

    data object OnStartTourIntroClick : TourIntroIntent

    data object OnBackClick: TourIntroIntent

    data object ShowAlert : TourIntroIntent
}