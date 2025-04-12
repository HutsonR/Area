package com.blackcube.home

import androidx.lifecycle.viewModelScope
import com.blackcube.core.BaseViewModel
import com.blackcube.home.models.StartedQuest
import com.blackcube.home.store.models.HomeEffect
import com.blackcube.home.store.models.HomeIntent
import com.blackcube.home.store.models.HomeState
import com.blackcube.models.places.PlaceModel
import com.blackcube.remote.repository.tours.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    // placeRepository: PlaceRepository
) : BaseViewModel<HomeState, HomeEffect>(HomeState()) {

    private val mockPlacesItems = listOf(
        PlaceModel(
            id = "1",
            imageUrl = "https://cdn.culture.ru/images/86520020-d89d-52e8-892d-2604dcf623c9",
            title = "Тайны старого города",
            description = "Исследуйте скрытые уголки.",
            lat = 47.236384,
            lon = 39.710064
        ),
        PlaceModel(
            id = "2",
            imageUrl = "https://avatars.mds.yandex.net/i?id=5382b7c4d70620cbfe73cb3cb1b000d6363ed90e-9198383-images-thumbs&n=13",
            title = "По следам призраков",
            description = "Ночная прогулка по мистическим местам. А также ещё несколько слов для многоточья",
            lat = 24.001,
            lon = 12.001
        )
    )

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                val tours = tourRepository.getTours(MAX_TOUR_ITEMS.toString())
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
                        placesItems = mockPlacesItems
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