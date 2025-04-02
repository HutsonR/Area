package com.blackcube.models.tours

/**
 * Модель данных, представляющая историю в рамках тура
 *
 * @property id Уникальный идентификатор истории
 * @property title Название истории
 * @property description Описание истории
 * @property isCompleted Признак завершения изучения истории
 * @property lat Географическая широта местоположения истории
 * @property lon Географическая долгота местоположения истории
 */
data class HistoryModel(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val lat: Double,
    val lon: Double
)