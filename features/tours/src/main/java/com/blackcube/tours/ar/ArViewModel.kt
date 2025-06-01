package com.blackcube.tours.ar

import android.location.Location
import com.blackcube.core.BaseViewModel
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import com.blackcube.tours.ar.store.models.ArModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.max

@HiltViewModel
class ArViewModel @Inject constructor() : BaseViewModel<ArState, ArEffect>(ArState()) {

    init {
        modifyState { copy(arModelPaths = listArObjects) }
    }

    private fun updateLocation(lat: Double, lon: Double) {
        if (getState().selectedArModel == null) {
            findNearest(lat, lon)?.let {
                modifyState { copy(selectedArModel = it) }
            }
        }

        val nearest = getState().selectedArModel ?: return
        val deltaLat = METERS_ERROR / 111_000.0
        val deltaLon = METERS_ERROR / (111_000.0 * cos(nearest.lat.toRadians()))
        val delta = max(deltaLat, deltaLon)
        val inZone = (lat in (nearest.lat - delta)..(nearest.lat + delta)
                && lon in (nearest.lon - delta)..(nearest.lon + delta))

        modifyState { copy(inZone = inZone) }
    }

    private fun findNearest(lat: Double, lon: Double): ArModel? {
        return getState().arModels.minByOrNull { coord ->
            val results = FloatArray(1)
            Location.distanceBetween(
                lat, lon,
                coord.lat, coord.lon,
                results
            )
            results[0]
        }
    }

    fun setCoordinates(arModels: List<ArModel>) {
        modifyState { copy(arModels = arModels) }
    }

    fun handleIntent(intent: ArIntent) {
        when (intent) {
            ArIntent.OnBackClick -> effect(ArEffect.NavigateToBack)
            is ArIntent.UpdateLocation -> updateLocation(intent.lat, intent.lon)
            is ArIntent.OnNodeClick -> effect(ArEffect.NavigateWithId(intent.id))
        }
    }

    private fun Double.toRadians() = Math.toRadians(this)

    companion object {
        const val ARGUMENT_COORDINATES = "ar_coordinates"
        private const val METERS_ERROR = 15.0 // погрешность

        private val listArObjects = listOf(
            "models/cat.glb",
            "models/dragon.glb",
            "models/shiba.glb",
        )
    }
}