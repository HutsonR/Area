package com.blackcube.remote.api.tours

import com.blackcube.models.tours.TourModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
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

    @PUT("tours/{id}")
    suspend fun updateTour(
        @Path("id") id: String,
        @Body tour: TourModel
    ): Response<Unit>

}