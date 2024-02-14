package com.example.walletapp.Server

import android.content.Context
import com.example.walletapp.helper.PasswordStorageHelper
import org.web3j.crypto.Keys
import org.web3j.utils.Numeric
import java.math.BigInteger

fun GetMyAddr(context: Context):String {
    val ps = PasswordStorageHelper(context)
    val pubKey = ps.getData("MyPublicKey") ?: return ""
    val bigpub: BigInteger
    bigpub = String(pubKey)!!.toBigInteger(16)
    return Numeric.prependHexPrefix(Keys.getAddress(bigpub))//ADDRESS
}