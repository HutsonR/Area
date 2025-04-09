package com.blackcube.tours.common.models

/**
 * @property name Название трека
 * @property text Текст трека
 * @property durationMillis Длительность трека в миллисекундах
 * */
data class Track(
    val name: String,
    val text: String,
    val durationMillis: Float = 0F
)
