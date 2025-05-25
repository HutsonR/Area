package com.blackcube.remote.repository.tours

import com.blackcube.models.tours.TourModel
import com.blackcube.remote.api.tours.ToursApi
import retrofit2.Response
import javax.inject.Inject

interface TourRepository {
    suspend fun getTours(limit: String? = null): List<TourModel>
    suspend fun getTourById(id: String): TourModel?
    suspend fun startTour(tourId: String): Boolean
    suspend fun finishTour(tourId: String): Boolean
    suspend fun completeHistory(historyId: String): Boolean
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

    override suspend fun startTour(tourId: String): Boolean {
        return try {
            val response: Response<Unit> = toursApi.startTour(tourId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun finishTour(tourId: String): Boolean {
        return try {
            val response: Response<Unit> = toursApi.finishTour(tourId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun completeHistory(historyId: String): Boolean {
        return try {
            val response: Response<Unit> = toursApi.completeHistory(historyId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}