package com.blackcube.home.store.models

sealed interface HomeEffect {
    data class NavigateToTourIntro(val id: String) : HomeEffect
    data class NavigateToPlaceIntro(val id: String) : HomeEffect
    data class NavigateToAllCards(val cardType: String) : HomeEffect
    data object ShowAlert : HomeEffect
}
