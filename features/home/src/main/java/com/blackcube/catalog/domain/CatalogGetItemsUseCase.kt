package com.blackcube.catalog.domain

import com.blackcube.catalog.models.CatalogItem
import com.blackcube.catalog.models.CatalogType
import kotlinx.coroutines.delay
import javax.inject.Inject

class CatalogGetItemsUseCase @Inject constructor(
    // tourRepository: TourRepository,
    // placeRepository: PlaceRepository
) {
    private val mockTourItems = listOf(
        CatalogItem.TourItem(
            id = "1",
            imageUrl = "https://cdn.culture.ru/images/86520020-d89d-52e8-892d-2604dcf623c9",
            title = "Тайны старого города",
            description = "Исследуйте скрытые уголки.",
            duration = "2 часа",
            isAR = true
        ),
        CatalogItem.TourItem(
            id = "2",
            imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
            title = "Легенды подземелий",
            description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого.",
            duration = "1.5 часа",
            isAR = false
        )
    )

    private val mockPlaceItems = listOf(
        CatalogItem.PlaceItem(
            id = "3",
            imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
            title = "Легенды подземелий",
            description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого."
        )
    )

    suspend fun getItems(contentType: CatalogType): List<CatalogItem> {
        delay(1000) // todo типа получаем (потом заменить на реальное получение)

        return when (contentType) {
            CatalogType.TOURS -> {
                mockTourItems
            }
            CatalogType.PLACES -> {
                mockPlaceItems
            }
        }
    }

    // todo наверное будет какой-то маппер в CatalogItem
}