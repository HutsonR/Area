package com.blackcube.places.store.models

import com.blackcube.models.places.PlaceModel

data class PlaceIntroState(
    val placeModel: PlaceModel? = null,
    val isLoading: Boolean = false
)