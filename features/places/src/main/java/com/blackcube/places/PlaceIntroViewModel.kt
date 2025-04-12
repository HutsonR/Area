package com.blackcube.places

import androidx.lifecycle.viewModelScope
import com.blackcube.common.utils.map.MapUseCase
import com.blackcube.core.BaseViewModel
import com.blackcube.places.store.models.PlaceIntroEffect
import com.blackcube.places.store.models.PlaceIntroIntent
import com.blackcube.places.store.models.PlaceIntroState
import com.blackcube.remote.repository.places.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceIntroViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val mapUseCase: MapUseCase
) : BaseViewModel<PlaceIntroState, PlaceIntroEffect>(PlaceIntroState()) {

    fun fetchPlace(placeId: String) {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                val place = placeRepository.getPlaceById(placeId)
                modifyState {
                    copy(
                        placeModel = place
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(PlaceIntroEffect.ShowAlert)
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    fun handleIntent(placeIntroIntent: PlaceIntroIntent) {
        when(placeIntroIntent) {
            PlaceIntroIntent.OnBackClick -> effect(PlaceIntroEffect.NavigateToBack)
            PlaceIntroIntent.OnShowMapClick -> effect(
                PlaceIntroEffect.ShowMap(
                    mapUseCase.createMapRequest(
                        getState().placeModel?.lat,
                        getState().placeModel?.lon
                    )
                )
            )
            PlaceIntroIntent.ShowAlert -> effect(PlaceIntroEffect.ShowAlert)
        }
    }
}