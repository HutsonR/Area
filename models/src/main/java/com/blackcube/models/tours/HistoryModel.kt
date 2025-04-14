package com.blackcube.models.tours

/**
 * Модель данных, представляющая историю в рамках тура
 *
 * @property id Уникальный идентификатор
 * @property ordinalNumber Порядковый номер
 * @property title Название
 * @property description Описание
 * @property isCompleted Признак завершения изучения
 * @property lat Географическая широта местоположения
 * @property lon Географическая долгота местоположения
 */
data class HistoryModel(
    val id: String,
    val ordinalNumber: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val lat: Double,
    val lon: Double
)