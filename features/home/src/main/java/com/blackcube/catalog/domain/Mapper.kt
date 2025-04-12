package com.blackcube.catalog.domain

import com.blackcube.catalog.models.CatalogItem
import com.blackcube.models.places.PlaceModel
import com.blackcube.models.tours.TourModel

fun List<TourModel>.mapToTourItems() = this.map {
    it.mapToTourItem()
}

fun TourModel.mapToTourItem() = CatalogItem.TourItem(
    id = this.id,
    imageUrl = this.imageUrl,
    title = this.title,
    description = this.description,
    duration = this.duration,
    isAR = this.isAR
)

fun List<PlaceModel>.mapToPlaceItems() = this.map {
    it.mapToPlaceItem()
}

fun PlaceModel.mapToPlaceItem() = CatalogItem.PlaceItem(
    id = this.id,
    imageUrl = this.imageUrl,
    title = this.title,
    description = this.description,
)