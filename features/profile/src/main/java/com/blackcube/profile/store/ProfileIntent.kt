package com.blackcube.profile.store

sealed interface ProfileIntent {
    data object GoBack : ProfileIntent
    data object OnLogout : ProfileIntent
}