package com.blackcube.home

import com.blackcube.core.BaseViewModel
import com.blackcube.home.models.TourModel
import com.blackcube.home.store.models.HomeEffect
import com.blackcube.home.store.models.HomeIntent
import com.blackcube.home.store.models.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : BaseViewModel<HomeState, HomeEffect>(HomeState()) {

    init {
        modifyState { copy(
            lists = listOf(
                TourModel(
                    id = "1",
                    imageUrl = "https://cdn.culture.ru/images/86520020-d89d-52e8-892d-2604dcf623c9",
                    title = "Тайны старого города",
                    description = "Исследуйте скрытые уголки.",
                    duration = "2 часа",
                    isAR = true
                ),
                TourModel(
                    id = "2",
                    imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
                    title = "Легенды подземелий",
                    description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого.",
                    duration = "1.5 часа",
                    isAR = false
                ),
                TourModel(
                    id = "3",
                    imageUrl = "https://avatars.mds.yandex.net/i?id=5382b7c4d70620cbfe73cb3cb1b000d6363ed90e-9198383-images-thumbs&n=13",
                    title = "По следам призраков",
                    description = "Ночная прогулка по мистическим местам с элементами AR.",
                    duration = "2.5 часа",
                    isAR = true
                ),
                TourModel(
                    id = "4",
                    imageUrl = "https://i.pinimg.com/736x/03/29/33/032933424b224925dc0e169d92ee9bf2.jpg",
                    title = "Архитектурные шедевры",
                    description = "Познакомьтесь.",
                    duration = "3 часа",
                    isAR = false
                ),
                TourModel(
                    id = "5",
                    imageUrl = "https://i.pinimg.com/originals/41/e7/5e/41e75ec75da14aef89185f4b89944878.jpg",
                    title = "Секретные лаборатории",
                    description = "Захватывающий квест с элементами дополненной реальности и научных загадок.",
                    duration = "2 часа",
                    isAR = true
                )
            )
        ) }
    }

    fun handleIntent(homeIntent: HomeIntent) {
        when(homeIntent) {
            is HomeIntent.OnExcursionClick -> {
                effect(HomeEffect.NavigateToExcursion(homeIntent.id))
            }
        }
    }
}