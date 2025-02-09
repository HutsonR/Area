package com.blackcube.area

sealed class Screens(val route: String) {
    data object MainScreen : Screens("home")
}