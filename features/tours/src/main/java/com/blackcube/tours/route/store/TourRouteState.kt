package com.blackcube.tours.route.store

import com.blackcube.models.tours.HistoryModel
import com.blackcube.tours.common.components.MapPoint
import com.yandex.mapkit.geometry.Point

data class TourRouteState(
    val id: String = "",
    val isAR: Boolean = false,
    val isLoading: Boolean = true,
    val selectedHistory: HistoryModel? = null,
    val currentLocation: Point? = null,
    val isTourStarted: Boolean = false,
    val histories: List<HistoryModel> = emptyList(),
    val mapPoints: List<MapPoint> = emptyList()
)