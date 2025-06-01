package com.blackcube.tours.ar.store.models

data class ArModel(
    val id: String,
    val lat: Double,
    val lon: Double,
    val content: String?,
    val type: ArType
)