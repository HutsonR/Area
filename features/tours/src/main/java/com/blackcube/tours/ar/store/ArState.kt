package com.blackcube.tours.ar.store

import com.blackcube.tours.ar.store.models.ArModel

data class ArState(
    val arModelPaths: List<String> = emptyList(),
    val arModels: List<ArModel> = emptyList(),
    val selectedArModel: ArModel? = null,
    val inZone: Boolean = false
)