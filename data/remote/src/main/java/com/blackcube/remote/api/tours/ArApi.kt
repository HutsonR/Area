package com.blackcube.remote.api.tours

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface ArApi {

    @POST("tours/ar/{arObjectId}/scan")
    suspend fun scanAr(
        @Path("arObjectId") id: String
    ): Response<Unit>

}