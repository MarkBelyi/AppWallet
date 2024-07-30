package com.example.walletapp.helper

import java.math.BigInteger
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec


// ЭТО ОРИГИНАЛ НЕ УДАЛАЯТЬ

object DESCrypt{
    // шифрование
    fun encrypt(input:String): ByteArray {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(byteArrayOf(0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111))
        val key: Key? = kf.generateSecret(keySpec)
        c.init(Cipher.ENCRYPT_MODE, key)
        val encrypt = c.doFinal(input.toByteArray())
        return encrypt
    }

    fun encrypt(input:BigInteger): ByteArray {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(byteArrayOf(0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111))
        val key: Key? = kf.generateSecret(keySpec)
        c.init(Cipher.ENCRYPT_MODE, key)
        val encrypt = c.doFinal(input.toByteArray())
        return encrypt
    }

    // дешифрование
    fun decrypt(input:ByteArray): ByteArray {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(byteArrayOf(0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111,0x48, 101, 108, 108, 111))
        val key: Key? = kf.generateSecret(keySpec)
        c.init(Cipher.DECRYPT_MODE, key)
        return try {
            val encrypt = c.doFinal(input)
            encrypt
        } catch (e: Exception) {
            byteArrayOf(108)
        }
    }
}

fun isBigInteger(str: String): Boolean {
    return try {
        BigInteger(str)
        true
    } catch (e: NumberFormatException) {
        false
    }
}

