package com.blackcube.remote.repository.tours

import com.blackcube.models.tours.TourModel
import com.blackcube.remote.api.tours.ToursApi
import retrofit2.Response
import javax.inject.Inject

interface TourRepository {
    suspend fun getTours(limit: String? = null): List<TourModel>
    suspend fun getTourById(id: String): TourModel?
    suspend fun updateTour(id: String, tour: TourModel): Boolean
}

class TourRepositoryImpl @Inject constructor(
    private val toursApi: ToursApi
) : TourRepository {

    override suspend fun getTours(limit: String?): List<TourModel> {
        return toursApi.getTours(limit)
    }

    override suspend fun getTourById(id: String): TourModel? {
        return try {
            toursApi.getTourById(id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateTour(id: String, tour: TourModel): Boolean {
        return try {
            val response: Response<Unit> = toursApi.updateTour(id, tour)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}