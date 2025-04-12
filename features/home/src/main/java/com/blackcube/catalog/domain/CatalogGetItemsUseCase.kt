package com.blackcube.catalog.domain

import com.blackcube.catalog.models.CatalogItem
import com.blackcube.catalog.models.CatalogType
import com.blackcube.remote.repository.tours.TourRepository
import javax.inject.Inject

class CatalogGetItemsUseCase @Inject constructor(
    private val tourRepository: TourRepository,
    // placeRepository: PlaceRepository
) {
    private val mockPlaceItems = listOf(
        CatalogItem.PlaceItem(
            id = "3",
            imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
            title = "Легенды подземелий",
            description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого."
        )
    )

    suspend fun getItems(contentType: CatalogType): List<CatalogItem> {
        val tours = tourRepository.getTours()

        return when (contentType) {
            CatalogType.TOURS -> {
                tours.mapToTourItems()
            }
            CatalogType.PLACES -> {
                mockPlaceItems
            }
        }
    }
}