package com.blackcube.tours.ar

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.blackcube.core.BaseViewModel
import com.blackcube.remote.models.ar.TourCommentArApiModel
import com.blackcube.remote.repository.tours.ArRepository
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import com.blackcube.tours.ar.store.models.ArModel
import com.blackcube.tours.ar.store.models.ArType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias arArgs = Pair<String, List<ArModel>>

@HiltViewModel
class ArViewModel @Inject constructor(
    private val arRepository: ArRepository
) : BaseViewModel<ArState, ArEffect>(ArState()) {

    init {
        modifyState { copy(arModelPaths = listArObjects) }
    }

    private fun updateLocation(lat: Double, lon: Double) {
        if (getState().selectedObjectModels.isEmpty()) {
            findNearestObject(lat, lon)?.let {
                val arTextModels = getState().arModels.filter { it.type == ArType.TEXT }
                modifyState { copy(selectedObjectModels = listOf(it) + arTextModels) }
            }
        }
    }

    private fun findNearestObject(lat: Double, lon: Double): ArModel? {
        return getState().arModels
            .filter { it.type == ArType.OBJECT }
            .minByOrNull { coord ->
            val results = FloatArray(1)
            Location.distanceBetween(
                lat, lon,
                coord.lat, coord.lon,
                results
            )
            results[0]
        }
    }

    fun setDataFromArgument(args: arArgs) {
        modifyState {
            copy(
                tourId = args.first,
                arModels = args.second
            )
        }
    }

    fun handleIntent(intent: ArIntent) {
        when (intent) {
            ArIntent.OnBackClick -> effect(ArEffect.NavigateToBack)
            is ArIntent.UpdateArLocation -> updateLocation(intent.lat, intent.lon)
            is ArIntent.OnObjectNodeClick -> effect(ArEffect.NavigateWithId(intent.id))
            is ArIntent.SaveComment -> saveComment(intent.arModel)
            is ArIntent.UpdateGpsLocation -> {}
        }
    }

    private fun saveComment(arModel: ArModel) {
        viewModelScope.launch {
            arModel.content ?: return@launch
            val apiModel = TourCommentArApiModel(
                tourId = getState().tourId,
                text = arModel.content,
                lat = arModel.lat,
                lon = arModel.lon
            )
            arRepository.addCommentAr(apiModel)
        }
    }

    companion object {
        const val ARGUMENT_COORDINATES = "ar_coordinates"

        private val listArObjects = listOf(
            "models/cat.glb",
            "models/dragon.glb",
            "models/shiba.glb",
        )
    }
}