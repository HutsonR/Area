package com.blackcube.core.navigation

import android.net.Uri

sealed class Screens(val route: String) {
    data object MainScreen : Screens("home")
    data object TourIntroScreen : Screens("tourIntro") {
        fun createRoute(id: String) = "tourIntro/${Uri.encode(id)}"
    }
    data object TourRouteScreen : Screens("tourRoute") {
        fun createRoute(id: String) = "tourRoute/${Uri.encode(id)}"
    }
}