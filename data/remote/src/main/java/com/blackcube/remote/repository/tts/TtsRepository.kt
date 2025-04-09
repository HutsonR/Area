package com.blackcube.remote.repository.tts

import com.blackcube.remote.BuildConfig
import com.blackcube.remote.api.tts.TtsApi
import com.blackcube.remote.models.tts.TtsApiModel
import com.blackcube.remote.repository.tts.TtsRepositoryImpl.Companion.DEFAULT_TTS_VOICE_ID
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

interface TtsRepository {
    /**
     * @param voiceId Идентификатор голоса
     * @param ttsApiModel See [TtsApiModel]
     * */
    suspend fun convertTextToSpeech(
        voiceId: String = DEFAULT_TTS_VOICE_ID,
        ttsApiModel: TtsApiModel
    ): ByteArray?
}

class TtsRepositoryImpl @Inject constructor(
    private val ttsApi: TtsApi
): TtsRepository {

    override suspend fun convertTextToSpeech(
        voiceId: String,
        ttsApiModel: TtsApiModel
    ): ByteArray? {
        return try {
            val response: Response<ResponseBody> =
                ttsApi.convertTextToSpeech(
                    apiKey = BuildConfig.ELEVENLABS_API_KEY,
                    voiceId = voiceId,
                    request = ttsApiModel,
                    outputFormat = DEFAULT_TTS_OUTPUT_FORMAT
                )
            if (response.isSuccessful) {
                response.body()?.byteStream()?.readBytes()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val DEFAULT_TTS_OUTPUT_FORMAT = "mp3_44100_128"
        const val DEFAULT_TTS_VOICE_ID = "Xb7hH8MSUJpSbSDYk0k2"
        const val DEFAULT_TTS_MODEL_ID = "eleven_multilingual_v2"
    }

}