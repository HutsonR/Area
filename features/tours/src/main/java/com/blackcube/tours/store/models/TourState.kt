package com.blackcube.tours.store.models

import com.blackcube.tours.models.HistoryModel

data class TourState(
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