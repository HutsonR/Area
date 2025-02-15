package com.blackcube.tours

import com.blackcube.core.BaseViewModel
import com.blackcube.tours.models.HistoryModel
import com.blackcube.tours.store.models.TourEffect
import com.blackcube.tours.store.models.TourIntent
import com.blackcube.tours.store.models.TourState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TourViewModel @Inject constructor(

) : BaseViewModel<TourState, TourEffect>(TourState()) {

    init {
        modifyState {
            copy(
                id = "",
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
                        title = "Какой-то заголовок истории 2 ",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 2",
                        lat = 24.0,
                        lon = 12.0
                    ),
                    HistoryModel(
                        id = "3",
                        title = "Какой-то заголовок истории 3",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 3",
                        lat = 24.0,
                        lon = 12.0
                    ),
                    HistoryModel(
                        id = "4",
                        title = "Какой-то заголовок истории 4",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 4",
                        lat = 24.0,
                        lon = 12.0
                    ),
                    HistoryModel(
                        id = "5",
                        title = "Какой-то заголовок истории 5",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 5",
                        lat = 24.0,
                        lon = 12.0
                    ),
                    HistoryModel(
                        id = "6",
                        title = "Какой-то заголовок истории 6",
                        description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 6",
                        lat = 24.0,
                        lon = 12.0
                    )
                )
            )
        }
    }

    private fun createMapRequest(): String {
        val lat = getState().selectedHistory?.lat
        val lon = getState().selectedHistory?.lon
        return "$BASE_MAP_REQUEST$lat,$lon"
    }

    fun handleIntent(tourIntent: TourIntent) {
        when(tourIntent) {
            is TourIntent.OnHistoryItemClick -> modifyState { copy(selectedHistory = tourIntent.item) }
            is TourIntent.OnStartTourClick -> effect(TourEffect.NavigateToStartTour(getState().id))
            TourIntent.OnBackClick -> effect(TourEffect.NavigateToBack)
            TourIntent.ShowAlert -> effect(TourEffect.ShowAlert)
            TourIntent.OnShowMapClick -> effect(TourEffect.ShowMap(createMapRequest()))
        }
    }

    companion object {
        private const val BASE_MAP_REQUEST = "geo:0,0?q="
    }
}