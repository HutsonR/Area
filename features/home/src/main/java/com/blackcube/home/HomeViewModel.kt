package com.blackcube.home

import androidx.lifecycle.viewModelScope
import com.blackcube.core.BaseViewModel
import com.blackcube.home.store.models.HomeEffect
import com.blackcube.home.store.models.HomeIntent
import com.blackcube.home.store.models.HomeState
import com.blackcube.models.places.PlaceModel
import com.blackcube.models.tours.TourModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    // tourRepository: TourRepository,
    // placeRepository: PlaceRepository
) : BaseViewModel<HomeState, HomeEffect>(HomeState()) {

    private val mockTourItems = listOf(
        TourModel(
            id = "1",
            imageUrl = "https://cdn.culture.ru/images/86520020-d89d-52e8-892d-2604dcf623c9",
            title = "Тайны старого города",
            description = "Исследуйте скрытые уголки.",
            duration = "2 часа",
            distance = "12 км.",
            isCompleted = false,
            isStarted = false,
            progress = 0F,
            isAR = true,
            histories = emptyList()
        ),
        TourModel(
            id = "2",
            imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
            title = "Легенды подземелий",
            description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого.",
            duration = "1.5 часа",
            distance = "12 км.",
            isCompleted = false,
            isStarted = true,
            progress = 0.0F,
            isAR = false,
            histories = emptyList()
        ),
        TourModel(
            id = "3",
            imageUrl = "https://avatars.mds.yandex.net/i?id=5382b7c4d70620cbfe73cb3cb1b000d6363ed90e-9198383-images-thumbs&n=13",
            title = "По следам призраков",
            description = "Ночная прогулка по мистическим местам с элементами AR.",
            duration = "2.5 часа",
            distance = "12 км.",
            isCompleted = false,
            isStarted = true,
            progress = 0.8F,
            isAR = true,
            histories = emptyList()
        ),
        TourModel(
            id = "4",
            imageUrl = "https://i.pinimg.com/736x/03/29/33/032933424b224925dc0e169d92ee9bf2.jpg",
            title = "Архитектурные шедевры",
            description = "Познакомьтесь.",
            duration = "3 часа",
            distance = "12 км.",
            isCompleted = false,
            isStarted = false,
            progress = 0.0F,
            isAR = false,
            histories = emptyList()
        ),
        TourModel(
            id = "5",
            imageUrl = "https://i.pinimg.com/originals/41/e7/5e/41e75ec75da14aef89185f4b89944878.jpg",
            title = "Секретные лаборатории",
            description = "Захватывающий квест с элементами дополненной реальности и научных загадок.",
            duration = "2 часа",
            distance = "12 км.",
            isCompleted = false,
            isStarted = false,
            progress = 0.0F,
            isAR = true,
            histories = emptyList()
        )
    )

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
                delay(1000) // todo типа получаем (потом заменить на реальное получение)
                val foundCurrentQuest = mockTourItems.findLast { it.isStarted }
                modifyState {
                    copy(
                        currentQuest = foundCurrentQuest,
                        tourItems = mockTourItems,
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
                getState().currentQuest?.let {
                    effect(HomeEffect.NavigateToTourIntro(it.id))
                } ?: effect(HomeEffect.ShowAlert)
            }

            HomeIntent.OnSeeStatsClick -> Unit // todo навигация в профиль со статистикой

            HomeIntent.Reload -> fetchData()
        }
    }
}