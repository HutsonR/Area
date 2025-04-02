package com.blackcube.catalog.store.models

sealed interface CatalogIntent {
    data class OnTourItemClick(
        val id: String
    ) : CatalogIntent

    data class OnPlaceItemClick(
        val id: String
    ) : CatalogIntent

    data object OnBackClick: CatalogIntent

    data object ShowAlert : CatalogIntent
}