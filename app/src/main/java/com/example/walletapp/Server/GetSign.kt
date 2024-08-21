package com.example.walletapp.Server

import android.content.Context
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import org.bouncycastle.util.encoders.Hex
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.math.BigInteger

fun Getsign(context: Context, mes: String): Array<String> { // Внимание! Функция сама добавит {}
    val rsva = arrayOf("", "", "", "") // r,s,v и адрес от алгоритма кривой
    val ps = PasswordStorageHelper((context))

    val privKey = ps.getData("MyPrivateKey") ?: return rsva
    val pubKey = ps.getData("MyPublicKey") ?: return rsva

    val bigpriv = BigInteger(privKey)
    val bigpub = BigInteger(pubKey)


    val msg = "{$mes}"

    val signature = Sign.signPrefixedMessage(
        msg.toByteArray(),
        ECKeyPair(bigpriv, bigpub)
    )// new version for api: /ece/
    rsva[3] = Numeric.prependHexPrefix(Keys.getAddress(bigpub)); //ADDRESS
    rsva[0] = "0x" + Hex.toHexString(signature.r)
    rsva[1] = "0x" + Hex.toHexString(signature.s)
    rsva[2] = "0x" + Hex.toHexString(signature.v)
    return rsva
}