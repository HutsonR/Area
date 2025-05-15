package com.blackcube.core.util.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    private val keyStore by lazy { createKeyStore() }

    fun createCipher(opmode: Int, iv: ByteArray? = null): Cipher {
        val key = getKey()
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        when (opmode) {
            Cipher.ENCRYPT_MODE -> cipher.init(opmode, key)
            Cipher.DECRYPT_MODE -> {
                val spec = IvParameterSpec(iv)
                cipher.init(opmode, key, spec)
            }
        }
        return cipher
    }

    private fun getKey(): Key =
        when (keyStore.containsAlias(KEY_ALIAS)) {
            true -> {
                val entry = keyStore.getEntry(KEY_ALIAS, null)
                (entry as KeyStore.SecretKeyEntry).secretKey
            }

            false -> generateKey()
        }

    private fun createKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEY_PROVIDER)
        keyStore.load(null)
        return keyStore
    }

    private fun generateKey(): Key {
        val keyGenerator = KeyGenerator.getInstance(KEY_GEN_ALGORITHM, KEY_PROVIDER)
        val spec = createSpec()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun createSpec(): KeyGenParameterSpec {
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        return KeyGenParameterSpec.Builder(KEY_ALIAS, purposes)
            .setKeySize(KEY_SIZE)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()
    }

    companion object {
        private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding"
        const val CIPHER_IV_SIZE = 16

        private const val KEY_GEN_ALGORITHM = "AES"
        private const val KEY_ALIAS = "AreaDataKeystoreAlias"
        private const val KEY_SIZE = 256
        private const val KEY_PROVIDER = "AndroidKeyStore"
    }

}