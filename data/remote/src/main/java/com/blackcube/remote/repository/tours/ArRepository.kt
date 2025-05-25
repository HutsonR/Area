package com.blackcube.remote.repository.tours

import com.blackcube.remote.api.tours.ArApi
import retrofit2.Response
import javax.inject.Inject

interface ArRepository {
    suspend fun scanAr(id: String): Boolean
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

}