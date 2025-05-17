package com.blackcube.remote.api.encryption

import com.blackcube.models.encryption.PublicKeyModel
import retrofit2.http.GET

interface EncryptionApi {

    @GET("encryption/publicKey")
    suspend fun getPublicKey(): PublicKeyModel

}