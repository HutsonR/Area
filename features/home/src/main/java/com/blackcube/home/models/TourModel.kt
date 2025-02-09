package com.blackcube.home.models

data class TourModel(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val duration: String,
    val isAR: Boolean
)
