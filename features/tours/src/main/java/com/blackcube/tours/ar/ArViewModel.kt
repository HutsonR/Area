package com.blackcube.tours.ar

import android.location.Location
import com.blackcube.core.BaseViewModel
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import com.blackcube.tours.ar.store.models.Coordinate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.max

@HiltViewModel
class ArViewModel @Inject constructor() : BaseViewModel<ArState, ArEffect>(ArState()) {

    private fun updateLocation(lat: Double, lon: Double) {
        if (getState().selectedCoordinate == null) {
            findNearest(lat, lon)?.let {
                modifyState { copy(selectedCoordinate = it) }
            }
        }

        val nearest = getState().selectedCoordinate ?: return
        val deltaLat = METERS_ERROR / 111_000.0
        val deltaLon = METERS_ERROR / (111_000.0 * cos(nearest.lat.toRadians()))
        val delta = max(deltaLat, deltaLon)
        val inZone = (lat in (nearest.lat - delta)..(nearest.lat + delta)
                && lon in (nearest.lon - delta)..(nearest.lon + delta))

        modifyState { copy(inZone = inZone) }
    }

    private fun findNearest(lat: Double, lon: Double): Coordinate? {
        return getState().coordinates.minByOrNull { coord ->
            val results = FloatArray(1)
            Location.distanceBetween(
                lat, lon,
                coord.lat, coord.lon,
                results
            )
            results[0]
        }
    }

    fun setCoordinates(coordinates: List<Coordinate>) {
        modifyState { copy(coordinates = coordinates) }
    }

    fun handleIntent(tourRouteIntent: ArIntent) {
        when (tourRouteIntent) {
            ArIntent.OnBackClick -> effect(ArEffect.NavigateToBack)
            is ArIntent.UpdateLocation -> updateLocation(tourRouteIntent.lat, tourRouteIntent.lon)
            is ArIntent.OnNodeClick -> effect(ArEffect.NavigateWithId(tourRouteIntent.id))
        }
    }

    private fun Double.toRadians() = Math.toRadians(this)

    companion object {
        const val ARGUMENT_COORDINATES = "ar_coordinates"
        private const val METERS_ERROR = 15.0 // погрешность
    }
}