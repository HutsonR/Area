package com.blackcube.places

import androidx.lifecycle.viewModelScope
import com.blackcube.common.utils.map.MapUseCase
import com.blackcube.core.BaseViewModel
import com.blackcube.models.places.PlaceModel
import com.blackcube.places.store.models.PlaceIntroEffect
import com.blackcube.places.store.models.PlaceIntroIntent
import com.blackcube.places.store.models.PlaceIntroState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceIntroViewModel @Inject constructor(
    // repository: PlaceRepository
    private val mapUseCase: MapUseCase
) : BaseViewModel<PlaceIntroState, PlaceIntroEffect>(PlaceIntroState()) {

    private val mockPlaceModel = PlaceModel(
        id = "123",
        imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
        title = "Легенды подземелий",
        description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого",
        lat = 47.236384,
        lon = 39.710064
    )

    fun fetchPlace(placeId: String) {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                delay(1000) // todo типа получаем (потом заменить на реальное получение)
                modifyState {
                    copy(
                        placeModel = mockPlaceModel
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