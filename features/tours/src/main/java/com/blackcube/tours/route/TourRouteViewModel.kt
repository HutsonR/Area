package com.blackcube.tours.route

import com.blackcube.core.BaseViewModel
import com.blackcube.tours.common.domain.MapUseCase
import com.blackcube.tours.common.models.HistoryModel
import com.blackcube.tours.route.store.TourRouteEffect
import com.blackcube.tours.route.store.TourRouteIntent
import com.blackcube.tours.route.store.TourRouteState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TourRouteViewModel @Inject constructor(
    private val mapUseCase: MapUseCase
) : BaseViewModel<TourRouteState, TourRouteEffect>(TourRouteState()) {

    init {
        modifyState {
            copy(
                id = "123456",
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

    fun handleIntent(tourRouteIntent: TourRouteIntent) {
        when (tourRouteIntent) {
            is TourRouteIntent.OnHistoryItemClick -> modifyState { copy(selectedHistory = tourRouteIntent.item) }
            TourRouteIntent.OnBackClick -> effect(TourRouteEffect.NavigateToBack)
            TourRouteIntent.OnShowMapClick -> effect(
                TourRouteEffect.ShowMap(
                    mapUseCase.createMapRequest(
                        getState().selectedHistory?.lat,
                        getState().selectedHistory?.lon
                    )
                )
            )
            TourRouteIntent.OnZoomMinusClick -> effect(TourRouteEffect.ZoomMinus)
            TourRouteIntent.OnZoomPlusClick -> effect(TourRouteEffect.ZoomPlus)
            TourRouteIntent.ShowAlert -> effect(TourRouteEffect.ShowAlert)
            TourRouteIntent.OnArClick -> effect(TourRouteEffect.SwitchArMode)
        }
    }
}