package com.example.walletapp.Server

import android.content.Context
import com.example.walletapp.helper.PasswordStorageHelper
import org.bouncycastle.util.encoders.Hex
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.math.BigInteger

fun Getsign(context: Context, mes:String):Array<String> { // Внимание! Функция сама добавит {}
    var rsva = arrayOf<String>("", "", "", "") // r,s,v и адрес от алгоритма кривой
    val ps = PasswordStorageHelper((context))

    val privKey = ps.getData("MyPrivateKey") ?: return rsva
    val pubKey = ps.getData("MyPublicKey") ?: return rsva

    // val ss:String = String(privKey)

    /*val bigpriv: BigInteger = BigInteger(privKey)
    val bigpub: BigInteger = BigInteger(pubKey)*/
    val bigpriv = BigInteger(privKey)
    val bigpub = BigInteger(pubKey)

    //val keyPair = ECKeyPair(bigpriv, bigpub)

    val msg = "{$mes}"

    //val msgHash: ByteArray = Hash.sha3(msg.toByteArray())
    //val signature = Sign.signMessage(msgHash, ECKeyPair(bigpriv, bigpub), false) // old version for api: /ec/

    val signature = Sign.signPrefixedMessage(msg.toByteArray(), ECKeyPair(bigpriv, bigpub))// new version for api: /ece/
    rsva[3]= Numeric.prependHexPrefix(Keys.getAddress(bigpub)); //ADDRESS
    rsva[0]="0x"+ Hex.toHexString(signature.r)
    rsva[1]="0x"+ Hex.toHexString(signature.s)
    rsva[2]="0x"+ Hex.toHexString(signature.v)
    return rsva
}