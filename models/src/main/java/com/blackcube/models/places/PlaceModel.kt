package com.blackcube.models.places

/**
 * Модель данных, представляющая место в городе.
 *
 * @property id Уникальный идентификатор места.
 * @property imageUrl URL изображения места.
 * @property title Название места.
 * @property description Описание места.
 * @property lat Географическая широта местоположения места.
 * @property lon Географическая долгота местоположения места.
 */
data class PlaceModel(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val lat: Double,
    val lon: Double
)
