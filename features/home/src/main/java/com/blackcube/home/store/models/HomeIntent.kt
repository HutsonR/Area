package com.blackcube.home.store.models

import com.blackcube.catalog.models.CatalogType

sealed interface HomeIntent {
    data class OnTourItemClick(
        val id: String
    ) : HomeIntent

    data class OnPlaceItemClick(
        val id: String
    ) : HomeIntent

    data class OnSeeAllCardsClick(
        val cardType: CatalogType
    ) : HomeIntent

    data object OnContinueTourClick : HomeIntent

    data object OnSeeStatsClick : HomeIntent

    data object Reload : HomeIntent
}