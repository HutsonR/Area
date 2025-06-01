package com.blackcube.remote.api.tours

import com.blackcube.remote.models.ar.TourCommentArApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ArApi {

    @POST("tours/ar/{arObjectId}/scan")
    suspend fun scanAr(
        @Path("arObjectId") id: String
    ): Response<Unit>

    @POST("tours/ar/comment")
    suspend fun addCommentAr(
        @Body tourCommentArApiModel: TourCommentArApiModel
    ): Response<Unit>

}