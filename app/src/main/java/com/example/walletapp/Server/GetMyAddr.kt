package com.example.walletapp.Server

import android.content.Context
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import org.web3j.crypto.Keys
import org.web3j.utils.Numeric
import java.math.BigInteger

fun bytesToHex(bytes: ByteArray): String {
    val hexChars = "0123456789ABCDEF"
    val result = StringBuilder(bytes.size * 2)
    bytes.forEach {
        val octet = it.toInt()
        result.append(hexChars[(octet and 0xF0) ushr 4])
        result.append(hexChars[(octet and 0x0F)])
    }
    return result.toString()
}

fun GetMyAddr(context: Context): String {
    val ps = PasswordStorageHelper(context)
    val pubKey = ps.getData("MyPublicKey") ?: return ""
    val hexString = bytesToHex(pubKey)
    val bigpub = BigInteger(hexString, 16)
    return Numeric.prependHexPrefix(Keys.getAddress(bigpub))//ADDRESS
}