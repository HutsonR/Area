package com.blackcube.tours.ar.store

sealed interface ArIntent {
    data object OnBackClick : ArIntent
    data class UpdateLocation(val lat: Double, val lon: Double) : ArIntent
    data class OnNodeClick(val id: String) : ArIntent
}