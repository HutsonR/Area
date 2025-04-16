package com.blackcube.remote.repository.tts

import com.blackcube.remote.api.tts.TtsApi
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

interface TtsRepository {
    /**
     * @param text Текст, который будет преобразован в речь
     * */
    suspend fun convertTextToSpeech(
        text: String
    ): ByteArray?
}

class TtsRepositoryImpl @Inject constructor(
    private val ttsApi: TtsApi
): TtsRepository {

    override suspend fun convertTextToSpeech(
        text: String
    ): ByteArray? {
        return try {
            val response: Response<ResponseBody> =
                ttsApi.synthesizeSpeech(
                    text = text
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

}