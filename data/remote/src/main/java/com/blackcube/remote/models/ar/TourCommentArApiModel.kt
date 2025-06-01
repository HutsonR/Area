package com.blackcube.remote.models.ar

data class TourCommentArApiModel(
    val tourId: String,
    val text: String,
    val lat: Double,
    val lon: Double
)
