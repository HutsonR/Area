package com.blackcube.remote.api.places

import com.blackcube.models.places.PlaceModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesApi {

    @GET("places")
    suspend fun getPlaces(
        @Query("limit") limit: String?
    ): List<PlaceModel>

    @GET("places/{id}")
    suspend fun getPlaceById(
        @Path("id") id: String
    ): PlaceModel

}