package com.blackcube.remote.repository.profile

import com.blackcube.models.profile.StatsModel
import com.blackcube.remote.api.profile.ProfileApi
import javax.inject.Inject

interface ProfileRepository {
    suspend fun getStats(): StatsModel?
}

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRepository {

    override suspend fun getStats(): StatsModel? =
        profileApi.getStats()

}