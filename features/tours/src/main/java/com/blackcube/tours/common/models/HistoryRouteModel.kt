package com.blackcube.tours.common.models

import com.blackcube.models.tours.HistoryModel

data class HistoryRouteModel(
    val id: String,
    val histories: List<HistoryModel>
)
