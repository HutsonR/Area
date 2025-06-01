package com.blackcube.models.tours

/**
 * Модель данных, представляющая объект для дополненной реальности (AR)
 *
 * @property id Уникальный идентификатор объекта
 * @property isScanned Флаг, указывающий, был ли найден объект
 * @property lat Географическая широта местоположения
 * @property text Комментарий пользователя. Null, если это мультяшка
 * @property lon Географическая долгота местоположения
 * */
data class ArObjectModel(
    val id: String,
    val isScanned: Boolean,
    val lat: Double,
    val text: String?,
    val lon: Double
)