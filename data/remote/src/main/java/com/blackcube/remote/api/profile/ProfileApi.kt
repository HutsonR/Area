package com.blackcube.remote.api.profile

import com.blackcube.models.profile.StatsModel
import retrofit2.http.GET

interface ProfileApi {

    @GET("stats")
    suspend fun getStats(): StatsModel?

}