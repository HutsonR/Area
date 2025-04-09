package com.blackcube.remote.models.tts

import com.google.gson.annotations.SerializedName

/**
 * @param text Текст, который будет преобразован в речь
 * @param modelId ID модели распознания
 * */
data class TtsApiModel(
    val text: String,
    @SerializedName("model_id")
    val modelId: String
)
