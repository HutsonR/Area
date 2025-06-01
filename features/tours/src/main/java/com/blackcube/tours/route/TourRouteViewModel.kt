package com.blackcube.tours.route

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.blackcube.common.ui.AlertData
import com.blackcube.common.utils.map.MapUseCase
import com.blackcube.core.BaseViewModel
import com.blackcube.models.tours.ArObjectModel
import com.blackcube.models.tours.HistoryModel
import com.blackcube.remote.repository.tours.ArRepository
import com.blackcube.remote.repository.tours.TourRepository
import com.blackcube.tours.R
import com.blackcube.tours.ar.store.models.ArModel
import com.blackcube.tours.ar.store.models.ArType
import com.blackcube.tours.common.components.MapPoint
import com.blackcube.tours.route.store.TourRouteEffect
import com.blackcube.tours.route.store.TourRouteIntent
import com.blackcube.tours.route.store.TourRouteState
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TourRouteViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val arRepository: ArRepository,
    private val mapUseCase: MapUseCase,
    @ApplicationContext private val appContext: Context
) : BaseViewModel<TourRouteState, TourRouteEffect>(TourRouteState()) {

    private fun createPartyList(): List<Party> {
        val party = Party(
            speed = 10f,
            maxSpeed = 30f,
            damping = 0.9f,
            angle = Angle.RIGHT - 45,
            spread = Spread.WIDE,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(30),
            position = Position.Relative(0.0, 0.5)
        )

        return listOf(
            party,
            party.copy(
                angle = party.angle - 90,
                position = Position.Relative(1.0, 0.5)
            )
        )
    }

    fun fetchHistories(tourId: String) {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                val tourModel = tourRepository.getTourById(tourId) ?: return@launch effect(
                    TourRouteEffect.ShowAlert(
                        AlertData(action = { effect(TourRouteEffect.NavigateToBack) })
                    )
                )
                val arFounded = tourModel.arObjects?.let {
                    calcArFounded(it)
                }

                modifyState {
                    copy(
                        tourModel = tourModel,
                        arFounded = arFounded,
                        routeProgress = calculateTourProgress(tourModel.histories),
                        mapPoints = tourModel.histories.toMapPoints()
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

            TourRouteIntent.OnArClick -> {
                val arObjectModels = getState().tourModel?.arObjects
                if (arObjectModels != null) {
                    effect(
                        TourRouteEffect.SwitchArMode(
                            getState().tourModel?.id ?: "",
                            arObjectModels.convertArObjectToArModel()
                        )
                    )
                } else {
                    effect(
                        TourRouteEffect.ShowAlert()
                    )
                }
            }

            is TourRouteIntent.OnMoveLocationClick -> setCurrentLocation(
                tourRouteIntent.lat,
                tourRouteIntent.lon
            )

            TourRouteIntent.StartTour -> onTourStarted()

            TourRouteIntent.StopTour -> {
                if (getState().tourModel?.isCompleted == true) {
                    effect(TourRouteEffect.NavigateToBack)
                } else {
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

            is TourRouteIntent.OnArObjectFound -> handleArObject(tourRouteIntent.id)
        }
    }

    private fun calcArFounded(arObjectModels: List<ArObjectModel>): Pair<Int, Int> {
        val founded = arObjectModels.count { it.isScanned }
        val total = arObjectModels.size
        return Pair(founded, total)
    }

    private fun List<ArObjectModel>.convertArObjectToArModel(): List<ArModel> =
        this
            .filter { it.isScanned.not() }
            .map {
                ArModel(
                    id = it.id,
                    lat = it.lat,
                    lon = it.lon,
                    content = it.text,
                    type = if (it.text.isNullOrBlank()) ArType.OBJECT else ArType.TEXT
                )
            }

    private fun handleArObject(id: String) {
        viewModelScope.launch {
            getState().tourModel?.run {
                val updated = copy(
                    arObjects = arObjects?.map { obj ->
                        if (obj.id == id) obj.copy(isScanned = true) else obj
                    }
                )

                modifyState { copy(tourModel = updated) }
            } ?: return@launch effect(TourRouteEffect.ShowAlert())

            arRepository.scanAr(id)
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
        state.value.tourModel?.histories?.find { it.id == itemId }?.let {
            modifyState { copy(selectedHistory = it) }
        }
    }

    private fun onTourStarted() {
        viewModelScope.launch {
            val tourModel = getState().tourModel ?: return@launch effect(TourRouteEffect.ShowAlert())
            val newTourModel = tourModel.copy(isStarted = true)
            if (tourModel.isStarted.not()) {
                tourRepository.startTour(newTourModel.id)
            }

            modifyState {
                copy(
                    tourModel = newTourModel,
                    isTourStarted = true
                )
            }
        }
    }

    private fun onCompleteHistory(itemId: String) {
        viewModelScope.launch {
            val foundHistory = state.value.tourModel?.histories?.find { it.id == itemId }
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
                    val newHistories = getState().tourModel?.histories?.map {
                        if (it.id == foundHistory.id) {
                            it.copy(isCompleted = true)
                        } else {
                            it
                        }
                    } ?: return@launch effect(TourRouteEffect.ShowAlert())

                    tourRepository.completeHistory(foundHistory.id)

                    val tourIsCompleted = newHistories.count { it.isCompleted }.let { countCompletedHistories ->
                        newHistories.size == countCompletedHistories
                    }
                    val newTourModel = getState().tourModel?.copy(
                        isCompleted = tourIsCompleted,
                        histories = newHistories
                    ) ?: return@launch effect(TourRouteEffect.ShowAlert())

                    modifyState {
                        copy(
                            tourModel = newTourModel,
                            routeProgress = calculateTourProgress(newTourModel.histories)
                        )
                    }
                    Toast.makeText(
                        appContext,
                        appContext.getString(R.string.history_route_location_complete),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (tourIsCompleted) {
                        tourRepository.finishTour(newTourModel.id)
                        Toast.makeText(
                            appContext,
                            appContext.getString(R.string.history_route_complete),
                            Toast.LENGTH_LONG
                        ).show()
                        effect(TourRouteEffect.ShowConfetti(createPartyList()))
                    }
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

    private fun List<HistoryModel>.toMapPoints() = this.map {
        MapPoint(
            id = it.id,
            latitude = it.lat,
            longitude = it.lon,
            title = it.title,
            ordinal = it.ordinalNumber
        )
    }

    companion object {
        const val ARGUMENT_SELECTED_AR_COORDINATE = "ar_selected_object_id"
        private const val DISTANCE_THRESHOLD = 100
    }
}