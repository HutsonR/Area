package com.blackcube.tours.ar.store

import com.blackcube.tours.ar.store.models.ArModel

sealed interface ArIntent {
    data object OnBackClick : ArIntent
    data class UpdateArLocation(val lat: Double, val lon: Double) : ArIntent
    data class UpdateGpsLocation(val lat: Double, val lon: Double) : ArIntent
    data class OnObjectNodeClick(val id: String) : ArIntent
    data class SaveComment(val arModel: ArModel) : ArIntent
}