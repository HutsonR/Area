package com.blackcube.remote.utils

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.inject.Inject

class EncryptionUtil @Inject constructor() {
    /**
     * Шифрование данных с помощью RSA алгоритма
     *
     * @param dataBytes Данные для шифрования в формате ByteArray
     * @param publicKeyBytes Публичный ключ в формате ByteArray
     *
     * @return Зашифрованная строка в формате Base64 или null, если было выброшенно исключение
     * */
    fun encryptRsa(
        dataBytes: ByteArray,
        publicKeyBytes: ByteArray
    ): String? {
        return try {
            val key = getKey(publicKeyBytes)
            val cipher = getCipher(key)

            return cipher.doFinal(dataBytes).let { encryptedBytes ->
                Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Получение публичного ключа с помощью KeyFactory
     *
     * @param publicKeyBytes изначально полученный ключ в соответствии со стандартом X.509
     *
     * @return [PublicKey] - публичный ключ для RSA алгоритма шифрования
     * */
    private fun getKey(publicKeyBytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * Получение объекта для шифрования данных
     *
     * @param publicKey публичный ключ под RSA алгоритм шифрования
     *
     * @return [Cipher] - настроенный метод для RSA шифрования
     * */
    private fun getCipher(publicKey: PublicKey): Cipher =
        Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, publicKey)
        }

    companion object {
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"

        private const val KEY_ALGORITHM = "RSA"
    }
}