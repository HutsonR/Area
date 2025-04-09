package com.blackcube.remote.api.tts

import com.blackcube.remote.models.tts.TtsApiModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TtsApi {

    /**
     * Запрос на синтез текста в речь.
     *
     * @param apiKey API ключ для авторизации
     * @param accept Заголовок Accept, по умолчанию "audio/mpeg"
     * @param voiceId ID голоса, который будет озвучивать
     * @param outputFormat Формат, в котором будет приходить озвучка. По умолчанию mp3_44100_128
     * @param request Объект запроса [TtsApiModel] с текстом и моделью
     * @return [Response] с данными аудио (mp3)
     */
    @POST("v1/text-to-speech/{voice_id}")
    @Streaming
    suspend fun convertTextToSpeech(
        @Header("xi-api-key") apiKey: String,
        @Header("Accept") accept: String = "audio/mpeg",
        @Path("voice_id") voiceId: String,
        @Query("output_format") outputFormat: String?,
        @Body request: TtsApiModel
    ): Response<ResponseBody>

}