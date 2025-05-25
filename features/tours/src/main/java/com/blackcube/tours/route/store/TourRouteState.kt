package com.blackcube.tours.route.store

import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel
import com.blackcube.tours.common.components.MapPoint
import com.yandex.mapkit.geometry.Point

data class TourRouteState(
    val isLoading: Boolean = false,
    val tourModel: TourModel? = null,
    val selectedHistory: HistoryModel? = null,
    val currentLocation: Point? = null,
    val routeProgress: Float = 0F,
    val isTourStarted: Boolean = false,
    val arFounded: Pair<Int, Int>? = null,
    val mapPoints: List<MapPoint> = emptyList()
)