package com.blackcube.tours.common.models

data class HistoryModel(
    val id: String,
    val title: String,
    val description: String,
    val lat: Double,
    val lon: Double
)