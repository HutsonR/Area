package com.blackcube.tours.route.store

import com.blackcube.tours.common.components.MapPoint
import com.blackcube.tours.common.models.HistoryModel

data class TourRouteState(
    val id: String = "",
    val isAR: Boolean = false,
    val isLoading: Boolean = true,
    val selectedHistory: HistoryModel? = null,
    val histories: List<HistoryModel> = emptyList(),
    val mapPoints: List<MapPoint> = emptyList()
)