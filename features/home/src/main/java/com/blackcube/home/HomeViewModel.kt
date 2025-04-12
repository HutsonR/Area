package com.blackcube.home

import androidx.lifecycle.viewModelScope
import com.blackcube.core.BaseViewModel
import com.blackcube.home.models.StartedQuest
import com.blackcube.home.store.models.HomeEffect
import com.blackcube.home.store.models.HomeIntent
import com.blackcube.home.store.models.HomeState
import com.blackcube.remote.repository.places.PlaceRepository
import com.blackcube.remote.repository.tours.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val placeRepository: PlaceRepository
) : BaseViewModel<HomeState, HomeEffect>(HomeState()) {

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                val tours = tourRepository.getTours(MAX_TOUR_ITEMS.toString())
                val places = placeRepository.getPlaces(MAX_PLACES_ITEMS.toString())

                val foundStartedQuest = tours.findLast { it.isStarted && it.isCompleted.not() }?.let { quest ->
                    val progress: Float = run {
                        val histories = quest.histories.also { if (it.isEmpty()) return@run 0F }
                        val countCompletedHistories = histories.count { it.isCompleted }
                        countCompletedHistories.toFloat() / histories.size
                    }

                    StartedQuest(
                        tourModel = quest,
                        progress = progress
                    )
                }
                modifyState {
                    copy(
                        currentStartedQuest = foundStartedQuest,
                        tourItems = tours,
                        placesItems = places
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(HomeEffect.ShowAlert)
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    fun handleIntent(homeIntent: HomeIntent) {
        when(homeIntent) {
            is HomeIntent.OnTourItemClick -> {
                effect(HomeEffect.NavigateToTourIntro(homeIntent.id))
            }

            is HomeIntent.OnPlaceItemClick -> {
                effect(HomeEffect.NavigateToPlaceIntro(homeIntent.id))
            }

            is HomeIntent.OnSeeAllCardsClick -> {
                effect(HomeEffect.NavigateToAllCards(homeIntent.cardType.name))
            }

            HomeIntent.OnContinueTourClick -> {
                getState().currentStartedQuest?.let {
                    effect(HomeEffect.NavigateToTourIntro(it.tourModel.id))
                } ?: effect(HomeEffect.ShowAlert)
            }

            HomeIntent.OnSeeStatsClick -> Unit // todo навигация в профиль со статистикой

            HomeIntent.Reload -> fetchData()
        }
    }

    companion object {
        const val MAX_TOUR_ITEMS = 5
        const val MAX_PLACES_ITEMS = 5
    }
}