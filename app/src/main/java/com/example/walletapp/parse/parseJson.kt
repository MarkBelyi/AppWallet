package com.example.walletapp.parse

import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Wallets
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

fun parseNetworksJsonWithGson(jsonString: String): List<Networks> {
    val gson = GsonBuilder().registerTypeAdapter(Networks::class.java, JsonDeserializer { json, _, _ ->
        val jsonObject = json.asJsonObject

        Networks(
            network_id = jsonObject.get("network_id").asInt,
            network_name = jsonObject.get("network_name").asString,
            link = jsonObject.get("link").asString,
            address_explorer = jsonObject.get("address_explorer").asString,
            tx_explorer = jsonObject.get("tx_explorer").asString,
            block_explorer = jsonObject.get("block_explorer").asString,
            // Проверяем на null и устанавливаем "" в качестве значения по умолчанию
            info = if (jsonObject.get("info").isJsonNull) "" else jsonObject.get("info").asString,
            status = jsonObject.get("status").asInt
        )
    }).create()

    val type = object : TypeToken<List<Networks>>() {}.type
    return gson.fromJson(jsonString, type)
}

/**Принимает строку и пытается сделать из неё JSONArray. Если строка оказывается не массивом, то молча возвратит пустой массив [] */
fun jsonArray(s:String): JSONArray { //Преобразует строку в массив JSON, а если всё плохо, то воротает пустой массив
    try {
        val jarr = JSONArray(s)
        return  jarr
    }catch (e:Exception){
        e.printStackTrace()
        println(e.toString()+"\n\n")
        return JSONArray("[]")
    }
}

/**Принимает строку и пытается сделать из неё JSONObject. Если строка оказывается фуфломицином, то молча возвратит пустой JSONObject */
fun jsonObject(s:String): JSONObject { //Преобразует строку в JSONObject, а если всё плохо, то воротает пустой JSONObject
    try {
        val jo = JSONObject(s)
        return  jo
    }catch (e:Exception){
        e.printStackTrace()
        println(e.toString()+"\n\n")
        return JSONObject("{}")
    }
}

/**Принимает строку и пытается сделать из неё List<Networks>. Если строка оказывается фуфломицином, то молча возвратит пустой List<Networks> */
fun parseNetworks(ss: String): List<Networks> {
    val jarr = jsonArray(ss)
    val gg = mutableListOf<Networks>()
    for (i in 0 until jarr.length())
    {val j = jarr.getJSONObject(i)
        gg.add(Networks(
            j["network_id"].toString().toInt(),// Ключевой уникальный параметр, должен быть, без него никак.
            j.optString("network_name", ""),
            j.optString("link",""),
            j.optString("address_explorer",""),
            j.optString("tx_explorer",""),
            j.optString("block_explorer",""),
            j.optString("info",""),
            j.optString("status","1").toInt()))
    }
    return gg
}

fun parseWallets(jsonString: String): List<Wallets> {
    val type = object : TypeToken<List<Wallets>>() {}.type
    return Gson().fromJson(jsonString, type)
}