package com.blackcube.catalog.store.models

import com.blackcube.catalog.models.CatalogItem
import com.blackcube.catalog.models.CatalogType

data class CatalogState(
    val currentCatalogType: CatalogType = CatalogType.TOURS,
    val items: List<CatalogItem> = emptyList(),
    val isLoading: Boolean = false
)