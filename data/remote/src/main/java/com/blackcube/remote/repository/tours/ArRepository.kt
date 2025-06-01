package com.blackcube.remote.repository.tours

import com.blackcube.remote.api.tours.ArApi
import com.blackcube.remote.models.ar.TourCommentArApiModel
import retrofit2.Response
import javax.inject.Inject

interface ArRepository {
    suspend fun scanAr(id: String): Boolean
    suspend fun addCommentAr(apiModel: TourCommentArApiModel): Boolean
}

class ArRepositoryImpl @Inject constructor(
    private val arApi: ArApi
) : ArRepository {

    override suspend fun scanAr(id: String): Boolean {
        return try {
            val response: Response<Unit> = arApi.scanAr(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addCommentAr(apiModel: TourCommentArApiModel): Boolean {
        return try {
            val response: Response<Unit> = arApi.addCommentAr(apiModel)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}