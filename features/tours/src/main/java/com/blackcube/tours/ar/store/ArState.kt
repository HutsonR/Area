package com.blackcube.tours.ar.store

import com.blackcube.tours.ar.store.models.Coordinate

data class ArState(
    val coordinates: List<Coordinate> = emptyList(),
    val selectedCoordinate: Coordinate? = null,
    val inZone: Boolean = false
)