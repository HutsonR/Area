package com.blackcube.tours.ar.store

import com.blackcube.tours.ar.store.models.ArModel

data class ArState(
    val tourId: String = "",
    val arModelPaths: List<String> = emptyList(),
    val arModels: List<ArModel> = emptyList(),
    val selectedObjectModels: List<ArModel> = emptyList(),
    val inZone: Boolean = false
)