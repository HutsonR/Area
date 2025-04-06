package com.blackcube.tours.intro

import androidx.lifecycle.viewModelScope
import com.blackcube.common.utils.map.MapUseCase
import com.blackcube.core.BaseViewModel
import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel
import com.blackcube.tours.intro.store.models.TourIntroEffect
import com.blackcube.tours.intro.store.models.TourIntroIntent
import com.blackcube.tours.intro.store.models.TourIntroState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourIntroViewModel @Inject constructor(
    // tourRepository: TourRepository,
    private val mapUseCase: MapUseCase
) : BaseViewModel<TourIntroState, TourIntroEffect>(TourIntroState()) {

    private val mockHistories = listOf(
        HistoryModel(
            id = "1",
            title = "Какой-то заголовок истории с локацией",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 1",
            isCompleted = false,
            lat = 47.236384,
            lon = 39.710064
        ),
        HistoryModel(
            id = "2",
            title = "Какой-то заголовок истории 2",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 2",
            isCompleted = false,
            lat = 24.001,
            lon = 12.001
        ),
        HistoryModel(
            id = "3",
            title = "Какой-то заголовок истории 3",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 3",
            isCompleted = false,
            lat = 23.998,
            lon = 12.002
        ),
        HistoryModel(
            id = "4",
            title = "Какой-то заголовок истории 4",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 4",
            isCompleted = false,
            lat = 24.002,
            lon = 11.999
        ),
        HistoryModel(
            id = "5",
            title = "Какой-то заголовок истории 5",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 5",
            isCompleted = false,
            lat = 23.997,
            lon = 12.000
        ),
        HistoryModel(
            id = "6",
            title = "Какой-то заголовок истории 6",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 6",
            isCompleted = false,
            lat = 24.000,
            lon = 12.003
        )
    )

    private val mockTourModel = TourModel(
        id = "123",
        imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
        title = "Легенды подземелий",
        description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого",
        isCompleted = false,
        duration = "1.5 часа",
        distance = "12 км.",
        isStarted = true,
        progress = 0.2F,
        isAR = true,
        histories = mockHistories
    )

    fun fetchTour(tourId: String) {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                delay(1000) // todo типа получаем (потом заменить на реальное получение)
                modifyState {
                    copy(
                        tourModel = mockTourModel,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(TourIntroEffect.ShowAlert)
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    private fun onStartTourClick() {
        getState().tourModel?.let {
            // todo нужно будет обновить старую запись о маршруте по id (TourModel). Отметить начатым
            effect(TourIntroEffect.NavigateToStartTour(it.id))
        } ?: run {
            effect(TourIntroEffect.ShowAlert)
        }
    }

    fun handleIntent(tourIntroIntent: TourIntroIntent) {
        when(tourIntroIntent) {
            is TourIntroIntent.OnHistoryItemClick -> modifyState { copy(selectedHistory = tourIntroIntent.item) }
            is TourIntroIntent.OnStartTourClick -> onStartTourClick()
            TourIntroIntent.OnBackClick -> effect(TourIntroEffect.NavigateToBack)
            TourIntroIntent.ShowAlert -> effect(TourIntroEffect.ShowAlert)
            TourIntroIntent.OnShowMapClick -> effect(
                TourIntroEffect.ShowMap(
                    mapUseCase.createMapRequest(
                        getState().selectedHistory?.lat,
                        getState().selectedHistory?.lon
                    )
                )
            )
        }
    }
}