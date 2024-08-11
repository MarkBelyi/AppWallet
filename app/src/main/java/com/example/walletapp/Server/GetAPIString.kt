package com.example.walletapp.Server

import android.content.Context
import com.example.walletapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


import java.util.concurrent.TimeUnit

suspend fun GetAPIString(con: Context, api:String, mes:String="", POST:Boolean=false):String = withContext(Dispatchers.IO) {
    val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val rsva = Getsign(con,mes)//ПОДПИСЬ содержимого запроса: r,s,v и адрес от алгоритма кривой
    val request: Request =
        if (POST) {
            val requestBody =  "{$mes}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            Request.Builder()
                .url(con.getString(R.string.base_url) + "ece/" + api) // запрос к серверу //https:/my.h2k.me/ece/uuid
                .addHeader("x-app-ec-from", rsva[3])
                .addHeader("x-app-ec-sign-r", rsva[0])
                .addHeader("x-app-ec-sign-s", rsva[1])
                .addHeader("x-app-ec-sign-v", rsva[2])
                .method("POST", requestBody)
                .build()
        } else {
            Request.Builder()
                .url(con.getString(R.string.base_url) + "ece/" + api) // запрос к серверу
                .addHeader("x-app-ec-from", rsva[3])
                .addHeader("x-app-ec-sign-r", rsva[0])
                .addHeader("x-app-ec-sign-s", rsva[1])
                .addHeader("x-app-ec-sign-v", rsva[2])
                .build()
        }

    var ss:String=""
    try {
        val call = client.newCall(request)
        var response: Response? =null
        response = call.execute();
        //val ktime= kotlin.system.measureTimeMillis{response = call.execute();}; log("APIusage",api+": "+ktime.toString())
        if (response==null) return@withContext ""
        if (!response!!.isSuccessful) return@withContext ""
        ss = response!!.body!!.string()
        if (ss.equals("{\"ERROR\":\"User was deleted\"}")) return@withContext ""
    } catch (e:Exception){
        e.printStackTrace()
        if (e.message!=null) println(e.message) else println("Server Read Error")
        return@withContext ""
    }
    return@withContext ss
}