package com.blackcube.tours.route

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.blackcube.common.ui.AlertData
import com.blackcube.core.BaseViewModel
import com.blackcube.tours.R
import com.blackcube.tours.common.components.MapPoint
import com.blackcube.tours.common.domain.MapUseCase
import com.blackcube.tours.common.models.HistoryModel
import com.blackcube.tours.route.store.TourRouteEffect
import com.blackcube.tours.route.store.TourRouteIntent
import com.blackcube.tours.route.store.TourRouteState
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourRouteViewModel @Inject constructor(
    private val mapUseCase: MapUseCase,
    @ApplicationContext private val appContext: Context
) : BaseViewModel<TourRouteState, TourRouteEffect>(TourRouteState()) {

    private val prepareHistories = listOf(
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
            lat = 47.240000,
            lon = 39.715000
        ),
        HistoryModel(
            id = "3",
            title = "Какой-то заголовок истории 3",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 3",
            lat = 47.232000,
            lon = 39.705000
        ),
        HistoryModel(
            id = "4",
            title = "Какой-то заголовок истории 4",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 4",
            lat = 47.244000,
            lon = 39.722000
        ),
        HistoryModel(
            id = "5",
            title = "Какой-то заголовок истории 5",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 5",
            lat = 47.230000,
            lon = 39.707000
        ),
        HistoryModel(
            id = "6",
            title = "Какой-то заголовок истории 6",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 6",
            lat = 47.238000,
            lon = 39.700000
        )
    )

    init {
        modifyState {
            copy(
                id = "123456",
                isAR = false,
                histories = prepareHistories,
                mapPoints = prepareHistories.toMapPoints()
            )
        }
    }

    fun handleIntent(tourRouteIntent: TourRouteIntent) {
        when (tourRouteIntent) {
            is TourRouteIntent.OnHistoryItemClick -> setSelectedHistory(tourRouteIntent.historyId)

            TourRouteIntent.OnBackClick -> effect(TourRouteEffect.NavigateToBack)

            TourRouteIntent.OnShowMapClick -> effect(
                TourRouteEffect.ShowMap(
                    mapUseCase.createMapRequest(
                        getState().selectedHistory?.lat,
                        getState().selectedHistory?.lon
                    )
                )
            )

            TourRouteIntent.ShowAlert -> effect(TourRouteEffect.ShowAlert())

            TourRouteIntent.OnArClick -> effect(TourRouteEffect.SwitchArMode)

            is TourRouteIntent.OnMoveLocationClick -> setCurrentLocation(
                tourRouteIntent.lat,
                tourRouteIntent.lon
            )

            TourRouteIntent.StartTour -> modifyState { copy(isTourStarted = true) }

            TourRouteIntent.StopTour -> {
                effect(
                    TourRouteEffect.ShowAlert(
                        AlertData(
                            title = appContext.getString(R.string.history_route_title_stop_alert_title),
                            message = appContext.getString(R.string.history_route_title_stop_alert_message),
                            isCancelable = true,
                            action = { modifyState { copy(isTourStarted = false) } }
                        )
                    )
                )
            }
        }
    }

    private fun setCurrentLocation(
        lat: Double?,
        lon: Double?
    ) {
        viewModelScope.launch {
            try {
                val currentLocation = if (lat == null || lon == null) {
                    mapUseCase.getCurrentPoint(context = appContext)
                } else {
                    Point(lat, lon)
                }
                modifyState { copy(currentLocation = currentLocation) }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(TourRouteEffect.ShowAlert())
            }
        }
    }

    private fun setSelectedHistory(itemId: String) {
        state.value.histories.find { it.id == itemId }?.let {
            modifyState { copy(selectedHistory = it) }
        }
    }

    private fun List<HistoryModel>.toMapPoints() = this.map {
        MapPoint(
            id = it.id,
            latitude = it.lat,
            longitude = it.lon,
            title = it.title,
        )
    }
}