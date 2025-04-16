package com.blackcube.remote.api.tts

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TtsApi {

    /**
     * Запрос на синтез текста в речь.
     *
     * @param text Текст, который будет преобразован в аудио
     */
    @POST("tts")
    @Headers("Accept: audio/x-wav")
    suspend fun synthesizeSpeech(
        @Body text: String
    ): Response<ResponseBody>

}