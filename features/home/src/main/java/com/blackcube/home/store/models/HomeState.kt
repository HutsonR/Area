package com.blackcube.home.store.models

import com.blackcube.models.places.PlaceModel
import com.blackcube.models.tours.TourModel

data class HomeState(
    val currentQuest: TourModel? = null,
    val tourItems: List<TourModel> = emptyList(),
    val placesItems: List<PlaceModel> = emptyList(),
    val isLoading: Boolean = false
)