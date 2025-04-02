package com.blackcube.catalog.store.models

sealed interface CatalogEffect {
    data class NavigateToTourIntro(val id: String) : CatalogEffect
    data class NavigateToPlaceIntro(val id: String) : CatalogEffect
    data object NavigateBack : CatalogEffect
    data object ShowAlert : CatalogEffect
}
