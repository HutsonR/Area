package com.blackcube.tours.common.domain

import javax.inject.Inject

class MapUseCase @Inject constructor() {

    fun createMapRequest(lat: Double?, lon: Double?): String {
        return "$BASE_MAP_REQUEST$lat,$lon"
    }

    companion object {
        private const val BASE_MAP_REQUEST = "geo:0,0?q="
    }
}