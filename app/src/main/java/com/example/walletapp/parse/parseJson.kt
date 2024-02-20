package com.example.walletapp.parse

import com.cri.wallet.database.entities.Networks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun parseNetworksJson(jsonString: String): List<Networks> {
    val gson = Gson()
    val type = object : TypeToken<List<Networks>>() {}.type
    return gson.fromJson(jsonString, type)
}