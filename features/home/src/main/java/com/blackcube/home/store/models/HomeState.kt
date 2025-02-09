package com.blackcube.home.store.models

import com.blackcube.home.models.TourModel

data class HomeState(
    val lists: List<TourModel> = emptyList()
)