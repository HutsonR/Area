package com.blackcube.core.navigation

import android.net.Uri

sealed class Screens(val route: String) {
    data object SplashScreen : Screens("splash")

    data object LoginScreen : Screens("login")

    data object RegisterScreen : Screens("register")

    data object MainScreen : Screens("home")

    data object TourIntroScreen : Screens("tourIntro") {
        fun createRoute(id: String) = "tourIntro/${Uri.encode(id)}"
    }

    data object PlaceIntroScreen : Screens("placeIntro") {
        fun createRoute(id: String) = "placeIntro/${Uri.encode(id)}"
    }

    data object AllCardsScreen : Screens("allCards") {
        fun createRoute(cardType: String) = "allCards/${Uri.encode(cardType)}"
    }

    data object TourRouteScreen : Screens("tourRoute") {
        fun createRoute(id: String) = "tourRoute/${Uri.encode(id)}"
    }
}