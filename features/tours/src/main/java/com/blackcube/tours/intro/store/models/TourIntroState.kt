package com.blackcube.tours.intro.store.models

import com.blackcube.tours.common.models.HistoryModel

data class TourIntroState(
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val duration: String = "",
    val distance: String = "",
    val isAR: Boolean = false,
    val isLoading: Boolean = true,
    val selectedHistory: HistoryModel? = null,
    val histories: List<HistoryModel> = emptyList()
)