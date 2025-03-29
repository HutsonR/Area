package com.blackcube.tours.intro

import com.blackcube.core.BaseViewModel
import com.blackcube.tours.common.domain.MapUseCase
import com.blackcube.tours.common.models.HistoryModel
import com.blackcube.tours.intro.store.models.TourIntroEffect
import com.blackcube.tours.intro.store.models.TourIntroIntent
import com.blackcube.tours.intro.store.models.TourIntroState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TourIntroViewModel @Inject constructor(
    private val mapUseCase: MapUseCase
) : BaseViewModel<TourIntroState, TourIntroEffect>(TourIntroState()) {

    init {
        modifyState {
            copy(
                id = "123",
                imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
                title = "Легенды подземелий",
                description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого",
                isCompleted = true,
                duration = "1.5 часа",
                distance = "12 км.",
                isAR = false,
                histories = listOf(
                    HistoryModel(
                        id = "1",
                        title = "Какой-то заголовок истории с локацией",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 1",
                        lat = 47.236384,
                        lon = 39.710064
                    ),
                    HistoryModel(
                        id = "2",
                        title = "Какой-то заголовок истории 2",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 2",
                        lat = 24.001,
                        lon = 12.001
                    ),
                    HistoryModel(
                        id = "3",
                        title = "Какой-то заголовок истории 3",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 3",
                        lat = 23.998,
                        lon = 12.002
                    ),
                    HistoryModel(
                        id = "4",
                        title = "Какой-то заголовок истории 4",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 4",
                        lat = 24.002,
                        lon = 11.999
                    ),
                    HistoryModel(
                        id = "5",
                        title = "Какой-то заголовок истории 5",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 5",
                        lat = 23.997,
                        lon = 12.000
                    ),
                    HistoryModel(
                        id = "6",
                        title = "Какой-то заголовок истории 6",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 6",
                        lat = 24.000,
                        lon = 12.003
                    )
                )
            )
        }
    }

    private fun onStartTourClick() {
        // todo в будущем пометить текущий тур, как начатый для отображения на главном экране
        effect(TourIntroEffect.NavigateToStartTour(getState().id))
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