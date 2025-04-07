package com.blackcube.tours.route

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.blackcube.common.ui.AlertData
import com.blackcube.common.utils.map.MapUseCase
import com.blackcube.core.BaseViewModel
import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel
import com.blackcube.tours.R
import com.blackcube.tours.common.components.MapPoint
import com.blackcube.tours.route.store.TourRouteEffect
import com.blackcube.tours.route.store.TourRouteIntent
import com.blackcube.tours.route.store.TourRouteState
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourRouteViewModel @Inject constructor(
    // tourRepository: TourRepository,
    private val mapUseCase: MapUseCase,
    @ApplicationContext private val appContext: Context
) : BaseViewModel<TourRouteState, TourRouteEffect>(TourRouteState()) {

    private val prepareHistories = listOf(
        HistoryModel(
            id = "1",
            title = "Какой-то заголовок истории с локацией",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 1",
            isCompleted = true,
            lat = 47.236384,
            lon = 39.710064
        ),
        HistoryModel(
            id = "2",
            title = "Какой-то заголовок истории 2",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 2",
            isCompleted = true,
            lat = 47.240000,
            lon = 39.715000
        ),
        HistoryModel(
            id = "3",
            title = "Какой-то заголовок истории 3",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 3",
            isCompleted = false,
            lat = 47.232000,
            lon = 39.705000
        ),
        HistoryModel(
            id = "4",
            title = "Какой-то заголовок истории 4",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 4",
            isCompleted = false,
            lat = 47.244000,
            lon = 39.722000
        ),
        HistoryModel(
            id = "5",
            title = "Какой-то заголовок истории 5",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 5",
            isCompleted = false,
            lat = 47.230000,
            lon = 39.707000
        ),
        HistoryModel(
            id = "6",
            title = "Какой-то заголовок истории 6",
            description = "Описание истории очень очень ооочень длинное, нужно просто создать эффект многоточья 6",
            isCompleted = false,
            lat = 47.238000,
            lon = 39.700000
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
        isAR = true,
        histories = prepareHistories
    )

    fun fetchHistories(tourId: String) {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                delay(1000) // todo типа получаем (потом заменить на реальное получение)
                modifyState {
                    copy(
                        tourId = tourId,
                        isAR = mockTourModel.isAR,
                        histories = mockTourModel.histories,
                        routeProgress = calculateTourProgress(mockTourModel.histories),
                        mapPoints = mockTourModel.histories.toMapPoints()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(
                    TourRouteEffect.ShowAlert(
                        AlertData(action = { effect(TourRouteEffect.NavigateToBack) })
                    )
                )
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    fun handleIntent(tourRouteIntent: TourRouteIntent) {
        when (tourRouteIntent) {
            is TourRouteIntent.OnHistoryItemClick -> setSelectedHistory(tourRouteIntent.historyId)

            is TourRouteIntent.OnHistoryCompleteClick -> onCompleteHistory(tourRouteIntent.historyId)

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
                            title = R.string.history_route_title_stop_alert_title,
                            message = R.string.history_route_title_stop_alert_message,
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

    private fun onCompleteHistory(itemId: String) {
        viewModelScope.launch {
            val foundHistory = state.value.histories.find { it.id == itemId }
                ?: return@launch effect(TourRouteEffect.ShowAlert())

            val currentPos = mapUseCase.getCurrentPoint(context = appContext)

            val distanceArray = FloatArray(1)
            Location.distanceBetween(
                currentPos.latitude,
                currentPos.longitude,
                foundHistory.lat,
                foundHistory.lon,
                distanceArray
            )
            val distance = distanceArray[0]

            when {
                !getState().isTourStarted -> {
                    effect(
                        TourRouteEffect.ShowAlert(
                            AlertData(
                                title = R.string.history_route_location_wrong_startTour_alert_title,
                                message = R.string.history_route_location_wrong_startTour_alert_message
                            )
                        )
                    )
                }

                foundHistory.isCompleted -> {
                    effect(
                        TourRouteEffect.ShowAlert(
                            AlertData(
                                title = R.string.history_route_location_success_alert_title,
                                message = R.string.history_route_location_success_alert_message,
                                actionButtonTitle = R.string.history_route_location_success_alert_button
                            )
                        )
                    )
                }

                distance.toInt() > DISTANCE_THRESHOLD -> {
                    effect(
                        TourRouteEffect.ShowAlert(
                            AlertData(
                                title = R.string.history_route_location_wrong_location_alert_title,
                                messageString = appContext.getString(
                                    R.string.history_route_location_wrong_location_alert_message,
                                    distance.toInt().toString()
                                )
                            )
                        )
                    )
                }

                else -> {
                    // todo сохранять на сервак
                    val newHistories = getState().histories.map {
                        if (it.id == foundHistory.id) {
                            it.copy(isCompleted = true)
                        } else {
                            it
                        }
                    }
                    modifyState {
                        copy(
                            histories = newHistories,
                            routeProgress = calculateTourProgress(newHistories)
                        )
                    }
                    Toast.makeText(
                        appContext,
                        appContext.getString(R.string.history_route_location_complete),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun calculateTourProgress(histories: List<HistoryModel>): Float {
        return if (histories.isNotEmpty()) {
            val countCompletedHistories = histories.count { it.isCompleted }
            countCompletedHistories.toFloat() / histories.size
        } else {
            0F
        }
    }

    companion object {
        private const val DISTANCE_THRESHOLD = 100
    }
}