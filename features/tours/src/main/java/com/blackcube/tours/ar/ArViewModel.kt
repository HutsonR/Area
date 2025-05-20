package com.blackcube.tours.ar

import com.blackcube.core.BaseViewModel
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.max

@HiltViewModel
class ArViewModel @Inject constructor() : BaseViewModel<ArState, ArEffect>(ArState()) {
    private val targetLat = 47.236830 // home 47.2367579 time 47.236830
    private val targetLon = 39.712408 // home 39.7067851 time 39.712408

    private val deltaLat = METERS_ERROR / 111_000.0
    private val deltaLon = METERS_ERROR / (111_000.0 * cos(targetLat.toRadians()))
    private val delta = max(deltaLat, deltaLon)

    // метод, который вызываем каждый кадр, передав реальные координаты
    private fun updateLocation(lat: Double, lon: Double) {
        val inZone =
            (lat in (targetLat - delta)..(targetLat + delta) && lon in (targetLon - delta)..(targetLon + delta))

        modifyState { copy(inZone = inZone) }
    }

    fun handleIntent(tourRouteIntent: ArIntent) {
        when (tourRouteIntent) {
            ArIntent.OnBackClick -> effect(ArEffect.NavigateToBack)
            is ArIntent.UpdateLocation -> updateLocation(tourRouteIntent.lat, tourRouteIntent.lon)
        }
    }

    private fun Double.toRadians() = Math.toRadians(this)

    companion object {
        private const val METERS_ERROR = 10.0 // погрешность
    }
}