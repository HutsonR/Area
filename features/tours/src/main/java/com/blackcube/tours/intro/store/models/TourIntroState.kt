package com.blackcube.tours.intro.store.models

import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel

data class TourIntroState(
    val tourModel: TourModel? = null,
    val isLoading: Boolean = true,
    val selectedHistory: HistoryModel? = null
)