package com.blackcube.tours.common.domain

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MapUseCase @Inject constructor() {

    fun createMapRequest(lat: Double?, lon: Double?): String {
        return "$BASE_MAP_REQUEST$lat,$lon"
    }

    suspend fun getCurrentPoint(context: Context): Point = suspendCancellableCoroutine { cont ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(Point(location.latitude, location.longitude))
                } else {
                    cont.resumeWithException(Exception("Текущая локация недоступна"))
                }
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    companion object {
        private const val BASE_MAP_REQUEST = "geo:0,0?q="
    }
}