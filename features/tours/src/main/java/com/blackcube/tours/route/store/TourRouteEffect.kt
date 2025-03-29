package com.blackcube.tours.route.store

import com.blackcube.common.ui.AlertData

sealed interface TourRouteEffect {
    data object NavigateToBack : TourRouteEffect

    data class ShowAlert(
        val alertData: AlertData = AlertData()
    ) : TourRouteEffect

    data class ShowMap(
        val request: String
    ) : TourRouteEffect

    data object SwitchArMode : TourRouteEffect
}
