package com.blackcube.catalog.domain

import com.blackcube.catalog.models.CatalogItem
import com.blackcube.catalog.models.CatalogType
import com.blackcube.remote.repository.places.PlaceRepository
import com.blackcube.remote.repository.tours.TourRepository
import javax.inject.Inject

class CatalogGetItemsUseCase @Inject constructor(
    private val tourRepository: TourRepository,
    private val placeRepository: PlaceRepository
) {
    suspend fun getItems(contentType: CatalogType): List<CatalogItem> {
        return when (contentType) {
            CatalogType.TOURS -> {
                tourRepository.getTours().mapToTourItems()
            }
            CatalogType.PLACES -> {
                placeRepository.getPlaces().mapToPlaceItems()
            }
        }
    }
}