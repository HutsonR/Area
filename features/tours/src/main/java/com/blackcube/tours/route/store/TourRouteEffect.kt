package com.blackcube.tours.route.store

import com.blackcube.common.ui.AlertData
import com.blackcube.tours.ar.store.models.Coordinate
import nl.dionsegijn.konfetti.core.Party

sealed interface TourRouteEffect {
    data object NavigateToBack : TourRouteEffect

    data class ShowAlert(
        val alertData: AlertData = AlertData()
    ) : TourRouteEffect

    data class ShowMap(
        val request: String
    ) : TourRouteEffect

    data class ShowConfetti(
        val party: List<Party>
    ) : TourRouteEffect

    data class SwitchArMode(
        val coordinates: List<Coordinate>
    ) : TourRouteEffect
}
