package com.blackcube.tours.common.models

import com.blackcube.models.tours.HistoryModel

data class HistoryRouteModel(
    val id: String,
    val progress: Float,
    val isTourStartedBefore: Boolean,
    val histories: List<HistoryModel>
)
