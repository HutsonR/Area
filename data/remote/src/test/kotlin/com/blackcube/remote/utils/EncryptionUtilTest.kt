package com.blackcube.remote.utils

import android.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import javax.crypto.Cipher

class EncryptionUtilTest {

    @Test
    fun testEncryptRsa() {
        val keyPair = generateRsaKeyPair()

        val originalText = "Секретное сообщение"

        val encryptedBase64 = EncryptionUtil().encryptRsa(originalText.toByteArray(), keyPair.public.encoded)
        assertNotNull(encryptedBase64)
        assertTrue(encryptedBase64!!.isNotBlank())

        val decryptedText = decryptWithPrivateKey(encryptedBase64, keyPair.private)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun testEncryptRsa_failed() {
        val keyPair = generateRsaKeyPair()

        val originalText = ""

        val publicKey = keyPair.public.encoded.copyOf(5) // поломанный ключ(с неправильным размером)
        val encryptedBase64 = EncryptionUtil().encryptRsa(originalText.toByteArray(), publicKey)
        assertNull(encryptedBase64)
    }

    private fun generateRsaKeyPair(): KeyPair {
        return KeyPairGenerator.getInstance(KEY_ALGORITHM).apply {
            initialize(KEY_SIZE)
        }.generateKeyPair()
    }

    private fun decryptWithPrivateKey(encryptedBase64: String, privateKey: PrivateKey): String {
        val encryptedBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, privateKey)
        }

        return cipher.doFinal(encryptedBytes).toString(charset = Charsets.UTF_8)
    }

    companion object {
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val KEY_ALGORITHM = "RSA"
        private const val KEY_SIZE = 2048
    }
}