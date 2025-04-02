package com.blackcube.places.store.models

sealed interface PlaceIntroIntent {
    data object OnShowMapClick : PlaceIntroIntent

    data object OnBackClick: PlaceIntroIntent

    data object ShowAlert : PlaceIntroIntent
}