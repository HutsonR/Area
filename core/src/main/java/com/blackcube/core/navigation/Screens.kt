package com.blackcube.core.navigation

import android.net.Uri

sealed class Screens(val route: String) {
    data object MainScreen : Screens("home")
    data object TourScreen : Screens("tour") {
        fun createRoute(id: String) = "tour/${Uri.encode(id)}"
    }
}