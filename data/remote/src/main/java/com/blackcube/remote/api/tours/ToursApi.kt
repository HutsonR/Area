package com.blackcube.remote.api.tours

import com.blackcube.models.tours.TourModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ToursApi {

    @GET("tours")
    suspend fun getTours(
        @Query("limit") limit: String?
    ): List<TourModel>

    @GET("tours/{id}")
    suspend fun getTourById(
        @Path("id") id: String
    ): TourModel

    @POST("tours/{id}/start")
    suspend fun startTour(
        @Path("id") id: String
    ): Response<Unit>

    @POST("tours/{id}/finish")
    suspend fun finishTour(
        @Path("id") id: String
    ): Response<Unit>

    @POST("tours/histories/{historyId}/complete")
    suspend fun completeHistory(
        @Path("historyId") id: String
    ): Response<Unit>

}