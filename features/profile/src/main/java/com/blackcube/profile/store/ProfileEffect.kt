package com.blackcube.profile.store

sealed interface ProfileEffect {
    data object NavigateToBack : ProfileEffect
}