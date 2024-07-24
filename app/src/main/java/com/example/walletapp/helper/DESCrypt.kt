package com.example.walletapp.helper


import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object DESCrypt {
    // Encryption
    fun encrypt(input: String): ByteArray {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(byteArrayOf(0x48, 101, 108, 108, 111, 0x48, 101, 108, 108, 111, 0x48, 101, 108, 108, 111, 0x48, 101, 108, 108, 111))
        val key: Key = kf.generateSecret(keySpec)
        c.init(Cipher.ENCRYPT_MODE, key)
        return c.doFinal(input.toByteArray(Charsets.UTF_8))
    }

    // Decryption
    fun decrypt(input: ByteArray): ByteArray {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(byteArrayOf(0x48, 101, 108, 108, 111, 0x48, 101, 108, 108, 111, 0x48, 101, 108, 108, 111, 0x48, 101, 108, 108, 111))
        val key: Key = kf.generateSecret(keySpec)
        c.init(Cipher.DECRYPT_MODE, key)
        return try {
            c.doFinal(input)
        } catch (e: Exception) {
            byteArrayOf()
        }
    }
}