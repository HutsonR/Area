package com.blackcube.tours.route.store

import com.blackcube.common.ui.AlertData
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

    data object SwitchArMode : TourRouteEffect
}
