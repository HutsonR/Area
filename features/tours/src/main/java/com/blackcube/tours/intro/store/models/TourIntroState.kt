package com.blackcube.tours.intro.store.models

import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel

data class TourIntroState(
    val tourModel: TourModel? = null,
    val tourProgress: Float = 0f,
    val isLoading: Boolean = true,
    val arFounded: Pair<Int, Int>? = null,
    val arScore: Pair<Int, Int>? = null,
    val selectedHistory: HistoryModel? = null
)