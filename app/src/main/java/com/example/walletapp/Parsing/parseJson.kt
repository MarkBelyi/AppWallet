package com.example.walletapp.Parsing

import android.util.Log
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.TokenInfo
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.Wallets
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun parseNetworksJsonWithGson(jsonString: String): List<Networks> {
    val gson =
        GsonBuilder().registerTypeAdapter(Networks::class.java, JsonDeserializer { json, _, _ ->
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

fun parseTokensInfo(json: String): List<TokenInfo> {
    val gson = Gson()
    val tokenType = object : TypeToken<List<TokenInfo>>() {}.type
    return gson.fromJson(json, tokenType)
}

/**Принимает строку и пытается сделать из неё JSONArray. Если строка оказывается не массивом, то молча возвратит пустой массив [] */
fun jsonArray(s: String): JSONArray { //Преобразует строку в массив JSON, а если всё плохо, то воротает пустой массив
    try {
        val jarr = JSONArray(s)
        return jarr
    } catch (e: Exception) {
        e.printStackTrace()
        println(e.toString() + "\n\n")
        return JSONArray("[]")
    }
}

/**Принимает строку и пытается сделать из неё JSONObject. Если строка оказывается фуфломицином, то молча возвратит пустой JSONObject */
fun jsonObject(s: String): JSONObject { //Преобразует строку в JSONObject, а если всё плохо, то воротает пустой JSONObject
    try {
        val jo = JSONObject(s)
        return jo
    } catch (e: Exception) {
        e.printStackTrace()
        println(e.toString() + "\n\n")
        return JSONObject("{}")
    }
}

/**Принимает строку и пытается сделать из неё List<Networks>. Если строка оказывается фуфломицином, то молча возвратит пустой List<Networks> */
fun parseNetworks(ss: String): List<Networks> {
    val jarr = jsonArray(ss)
    val gg = mutableListOf<Networks>()
    for (i in 0 until jarr.length()) {
        val j = jarr.getJSONObject(i)
        gg.add(
            Networks(
                j["network_id"].toString()
                    .toInt(),// Ключевой уникальный параметр, должен быть, без него никак.
                j.optString("network_name", ""),
                j.optString("link", ""),
                j.optString("address_explorer", ""),
                j.optString("tx_explorer", ""),
                j.optString("block_explorer", ""),
                j.optString("info", ""),
                j.optString("status", "1").toInt()
            )
        )
    }
    return gg
}

/**Принимает строку и пытается сделать из неё List<Wallets>, для обновления при заходе на страницу кошельков, и обработка на пустоту*/
fun parseWallets(ss: String): List<Wallets> {
    if (ss.isEmpty()) {
        return emptyList() // Возвращает пустой список, если ss пусто
    }
    val jsonString = if (ss == "{}") "[]" else ss
    val jarr = JSONArray(jsonString)
    val gg = mutableListOf<Wallets>()
    for (i in 0 until jarr.length()) {
        val j = jarr.getJSONObject(i)
        gg.add(
            Wallets(
                j["wallet_id"].toString().toInt(),
                j["network"].toString().toInt(),
                j.optString("myFlags", ""),
                j.optString("wallet_type", "0").toInt(),
                j.optString("name", ""),
                j.optString("info", ""),
                j.optString("addr", ""),
                j.optString("addr_info", ""),
                j.optString("myUNID", ""),
                j.optString("tokenShortNames", ""),
                j.optString("slist", ""),
                j.optString("minSignersCount", "1").toInt(),
                j.optString("group_id", "")
            ),
        )
    }
    return gg
}


fun parseTokens(ss: String): List<Tokens> {
    if (ss.isEmpty()) {
        return emptyList() // Возвращает пустой список, если ss пусто
    }
    val jsonString = if (ss == "{}") "[]" else ss
    val jarr = JSONArray(jsonString)
    val tokensList = mutableListOf<Tokens>()
    for (i in 0 until jarr.length()) {
        val j = jarr.getJSONObject(i)
        tokensList.add(
            Tokens(
                network_id = j["network"].toString().toInt(),
                name = j.optString("token", ""),
                addr = j.optString("addr", ""),
                myFlags = j.optString("myFlags", ""),
                decimals = j.optString("decimals", "6").toInt(),
                info = j.optString("info", ""),
                c = 0f, // Значения по умолчанию, поскольку JSON с сервака не содержит соответствующих данных
                cMin = 0f,
                cMax = 0f,
                cBase = 0f
            )
        )
    }
    return tokensList
}

fun parseSlist(response: String): Pair<String, Int> {
    return try {
        val slistArray = JSONArray(response)
        if (slistArray.length() > 0) {
            val slistObject = slistArray.getJSONObject(0).getString("slist")
            val slistJson = JSONObject(slistObject)
            val minSigns = slistJson.getString("min_signs").toDouble().toInt()
            val ecAddresses = slistJson.keys().asSequence()
                .filter { it != "min_signs" }
                .map { slistJson.getJSONObject(it).getString("ecaddress") }
                .joinToString(",")

            ecAddresses to minSigns
        } else {
            "" to 1
        }
    } catch (e: JSONException) {
        Log.e("JSON Error", "Error parsing JSON response: ${e.message}")
        "" to 1
    }
}