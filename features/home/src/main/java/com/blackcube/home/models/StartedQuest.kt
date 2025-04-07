package com.blackcube.home.models

import com.blackcube.models.tours.TourModel

data class StartedQuest(
    val tourModel: TourModel,
    val progress: Float
)
