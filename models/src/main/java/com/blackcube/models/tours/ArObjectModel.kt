package com.blackcube.models.tours

/**
 * Модель данных, представляющая объект для дополненной реальности (AR)
 *
 * @property id Уникальный идентификатор объекта
 * @property isScanned Флаг, указывающий, был ли найден объект
 * @property lat Географическая широта местоположения
 * @property lon Географическая долгота местоположения
 * @property points Кол-во очков, которое даёт скан модели
 * */
data class ArObjectModel(
    val id: String,
    val isScanned: Boolean,
    val lat: Double,
    val lon: Double,
    val points: Int
)