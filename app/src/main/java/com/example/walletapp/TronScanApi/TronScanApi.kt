package com.example.walletapp.TronScanApi

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun TronScanApi() {
    val apiKey = "bbd6e3b5-09ed-4d2a-b429-df66d18d6006"
    val endpoint = ""//TODO(Добавить BAse URL TronScanAPI)  + "block"

    try {
        val url = URL(endpoint)
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("TRON-PRO-API-KEY", apiKey)

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val responseBody = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    responseBody.append(line)
                }
                println(responseBody.toString())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}