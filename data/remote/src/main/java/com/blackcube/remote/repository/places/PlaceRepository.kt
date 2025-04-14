package com.blackcube.remote.repository.places

import com.blackcube.models.places.PlaceModel
import com.blackcube.remote.api.places.PlacesApi
import javax.inject.Inject

interface PlaceRepository {
    suspend fun getPlaces(limit: String? = null): List<PlaceModel>
    suspend fun getPlaceById(id: String): PlaceModel
}

class PlaceRepositoryImpl @Inject constructor(
    private val placesApi: PlacesApi
) : PlaceRepository {

    override suspend fun getPlaces(limit: String?): List<PlaceModel> =
        placesApi.getPlaces(limit)

    override suspend fun getPlaceById(id: String): PlaceModel =
        placesApi.getPlaceById(id)

}