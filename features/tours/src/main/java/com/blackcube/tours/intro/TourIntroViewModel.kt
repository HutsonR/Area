package com.blackcube.tours.intro

import androidx.lifecycle.viewModelScope
import com.blackcube.common.utils.map.MapUseCase
import com.blackcube.core.BaseViewModel
import com.blackcube.models.tours.ArObjectModel
import com.blackcube.remote.repository.tours.TourRepository
import com.blackcube.tours.intro.store.models.TourIntroEffect
import com.blackcube.tours.intro.store.models.TourIntroIntent
import com.blackcube.tours.intro.store.models.TourIntroState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourIntroViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val mapUseCase: MapUseCase
) : BaseViewModel<TourIntroState, TourIntroEffect>(TourIntroState()) {

    fun fetchTour(tourId: String) {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                val tourModel = tourRepository.getTourById(tourId) ?: return@launch effect(TourIntroEffect.ShowAlert)

                val progress: Float = run {
                    val histories = tourModel.histories.also { if (it.isEmpty()) return@run 0F }
                    val countCompletedHistories = histories.count { it.isCompleted }
                    countCompletedHistories.toFloat() / histories.size
                }
                val arFounded = tourModel.arObjects?.let {
                    calcArFounded(it)
                }
                val arScore = tourModel.arObjects?.let {
                    calcArScore(it)
                }

                modifyState {
                    copy(
                        tourModel = tourModel,
                        arFounded = arFounded,
                        arScore = arScore,
                        tourProgress = progress
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
            effect(TourIntroEffect.NavigateToStartTour(it.id))
        } ?: run {
            effect(TourIntroEffect.ShowAlert)
        }
    }

    private fun calcArFounded(arObjectModels: List<ArObjectModel>): Pair<Int, Int> {
        val founded = arObjectModels.count { it.isScanned }
        val total = arObjectModels.size
        return Pair(founded, total)
    }

    private fun calcArScore(arObjectModels: List<ArObjectModel>): Pair<Int, Int> {
        val totalSum = arObjectModels.sumOf { it.points }
        val foundSum = arObjectModels.filter { it.isScanned }.sumOf { it.points }

        return Pair(foundSum, totalSum)
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