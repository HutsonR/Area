package com.blackcube.authorization.storage

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.blackcube.authorization.session.TokenStorage
import com.blackcube.core.util.encryption.CryptoManager
import com.blackcube.core.util.encryption.CryptoManager.Companion.CIPHER_IV_SIZE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject

class EncryptedDataStoreTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) : TokenStorage {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("secure_prefs") }
    )

    private val cryptoManager = CryptoManager()

    override suspend fun save(token: String) {
        val encrypted = encrypt(token)
        dataStore.edit { prefs ->
            prefs[KEY_JWT_TOKEN] = encrypted
        }
    }

    override suspend fun get(): String? =
        dataStore.data
            .map { prefs -> prefs[KEY_JWT_TOKEN] }
            .firstOrNull()
            ?.let { decrypt(it) }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_JWT_TOKEN)
        }
    }

    private fun encrypt(plain: String): String {
        val cipher = cryptoManager.createCipher(Cipher.ENCRYPT_MODE)
        val iv = cipher.iv
        val cipherText = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        // сохраняем IV + данные вместе
        val combined = ByteBuffer.allocate(iv.size + cipherText.size)
            .put(iv)
            .put(cipherText)
            .array()
        return Base64.getEncoder().encodeToString(combined)
    }

    private fun decrypt(base64: String): String {
        val combined = Base64.getDecoder().decode(base64)
        // извлекаем IV (первые CIPHER_IV_SIZE байт)
        val iv = combined.copyOfRange(0, CIPHER_IV_SIZE)
        val cipherText = combined.copyOfRange(CIPHER_IV_SIZE, combined.size)

        val cipher = cryptoManager.createCipher(Cipher.DECRYPT_MODE, iv)
        val plain = cipher.doFinal(cipherText)
        return String(plain, Charsets.UTF_8)
    }

    companion object {
        private val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
    }
}