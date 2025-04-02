package com.blackcube.catalog

import androidx.lifecycle.viewModelScope
import com.blackcube.catalog.domain.CatalogGetItemsUseCase
import com.blackcube.catalog.models.CatalogType
import com.blackcube.catalog.store.models.CatalogEffect
import com.blackcube.catalog.store.models.CatalogIntent
import com.blackcube.catalog.store.models.CatalogState
import com.blackcube.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val catalogGetItemsUseCase: CatalogGetItemsUseCase
) : BaseViewModel<CatalogState, CatalogEffect>(CatalogState()) {

    fun setCatalogTypeAndFetch(catalogType: CatalogType) {
        modifyState {
            copy(currentCatalogType = catalogType)
        }
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                val resultItems = catalogGetItemsUseCase.getItems(getState().currentCatalogType)
                modifyState { copy(items = resultItems) }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(CatalogEffect.ShowAlert)
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    fun handleIntent(catalogIntent: CatalogIntent) {
        when (catalogIntent) {
            is CatalogIntent.OnPlaceItemClick -> effect(CatalogEffect.NavigateToPlaceIntro(catalogIntent.id))

            is CatalogIntent.OnTourItemClick -> effect(CatalogEffect.NavigateToTourIntro(catalogIntent.id))

            CatalogIntent.OnBackClick -> effect(CatalogEffect.NavigateBack)

            CatalogIntent.ShowAlert -> effect(CatalogEffect.ShowAlert)
        }
    }
}