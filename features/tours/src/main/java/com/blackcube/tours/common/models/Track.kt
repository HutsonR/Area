package com.blackcube.tours.common.models

/**
 * @property name Название трека
 * @property durationMillis Длительность трека в миллисекундах
 * @property url Ссылка на трек
 * */
data class Track(
    val name: String,
    val durationMillis: Float = 0F,
    val url: String
)
