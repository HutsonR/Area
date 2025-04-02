package com.blackcube.catalog.models

sealed class CatalogItem {
    abstract val id: String
    abstract val imageUrl: String
    abstract val title: String
    abstract val description: String

    data class TourItem(
        override val id: String,
        override val imageUrl: String,
        override val title: String,
        override val description: String,
        val duration: String,
        val isAR: Boolean
    ) : CatalogItem()

    data class PlaceItem(
        override val id: String,
        override val imageUrl: String,
        override val title: String,
        override val description: String
    ) : CatalogItem()
}