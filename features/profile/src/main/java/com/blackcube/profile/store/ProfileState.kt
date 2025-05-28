package com.blackcube.profile.store

import com.blackcube.models.profile.StatsModel

data class ProfileState(
    val isLoading: Boolean = true,
    val stats: StatsModel? = null
)