package com.example.walletapp.appViewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R
import com.example.walletapp.Server.GetAPIString
import com.example.walletapp.Server.Getsign
import com.example.walletapp.parse.jsonArray
import com.example.walletapp.parse.parseNetworks
import com.example.walletapp.parse.parseTokens
import com.example.walletapp.parse.parseWallets
import com.example.walletapp.registrationScreens.AuthMethod
import com.example.walletapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class appViewModel(private val repository: AppRepository, application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private val _selectedAuthMethod = MutableLiveData<AuthMethod>()
    private val selectedAuthMethod: LiveData<AuthMethod> = _selectedAuthMethod
    fun updateAuthMethod(authMethod: AuthMethod, context: Context) {
        val prefs = context.getSharedPreferences("AuthPreferences", Context.MODE_PRIVATE)
        prefs.edit().putString("AuthMethod", authMethod.name).apply()
        _selectedAuthMethod.value = authMethod
    }
    fun getAuthMethod(): LiveData<AuthMethod> = selectedAuthMethod
    fun getData(): LiveData<AuthMethod> {
        return selectedAuthMethod
    }



    //Tokens
    val allTokens: LiveData<List<Tokens>> = repository.allTokens.asLiveData()
    fun addTokens(context: Context) = viewModelScope.launch {
        val jsonString = GetAPIString(context, "tokens")
        val loadedTokens = parseTokens(jsonString)
        repository.addTokens(loadedTokens)
    }

    // Wallets and Tx
    val allWallets: LiveData<List<Wallets>> = repository.allWallets.asLiveData()
    val allTX: LiveData<List<TX>> = repository.allTX.asLiveData()

    fun signersList(context: Context, unid: String) = viewModelScope.launch {
        val apiResponse = GetAPIString(context, "get_wallet_slist/${unid}")
        println(apiResponse)
    }

    fun needSignTX(context: Context) = viewModelScope.launch {
        val apiResponse = GetAPIString(context, "tx_by_ec")
        if (apiResponse.isNotEmpty()) {
            try {
                val transactions = JSONArray(apiResponse)
                val txList = mutableListOf<TX>()
                for (i in 0 until transactions.length()) {
                    val txJson = transactions.getJSONObject(i)
                    val tokenParts = txJson.optString("token", "").split(":::")
                    val networkToken = tokenParts[0]
                    val tokenId = tokenParts.getOrElse(1) { "" }.split("###")[0]

                    val waitEC = txJson.optJSONArray("wait")?.let { waitArray ->
                        val waitList = mutableListOf<String>()
                        for (j in 0 until waitArray.length()) {
                            val waitObject = waitArray.optJSONObject(j)
                            if (waitObject != null) {
                                waitList.add(waitObject.optString("ecaddress", ""))
                            }
                        }
                        waitList.joinToString(",")
                    } ?: ""

                    val tx = TX(
                        unid = txJson.optString("unid", ""),
                        id = txJson.optInt("id", 0), // Парсинг ID
                        tx = txJson.optString("tx", ""), // Обработка null для tx
                        minsign = txJson.optString("min_sign", "1").toIntOrNull() ?: 1, // Парсинг min_sign
                        waitEC = waitEC, // EC Адреса подписантов, подписи которых ждёт транзакция
                        signedEC = "", // Подписи подписантов пока не обрабатываются, можно добавить аналогично waitEC при необходимости
                        network = networkToken.toIntOrNull() ?: 0, // Обработка network ID
                        token = tokenId,
                        to_addr = txJson.optString("to_addr", ""),
                        info = txJson.optString("info", ""), // Парсинг info
                        tx_value = txJson.optString("value", "0").replace(",", "").toDouble(), // Преобразование value в Double
                        value_hex = txJson.optString("value_hex", "0"), // Парсинг value_hex
                        init_ts = txJson.optLong("init_ts", 0L).toInt(), // Преобразование init_ts в Int
                        eMSG = "", // Обработка сообщения об ошибке при необходимости
                    )
                    txList.add(tx)
                }
                repository.insertAllTransactions(txList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAndStoreTransactions(context: Context) = viewModelScope.launch {
        val apiResponse = GetAPIString(context, "tx")
        if (apiResponse.isNotEmpty()) {
            try {
                val transactions = JSONArray(apiResponse)
                val txList = mutableListOf<TX>()
                for (i in 0 until transactions.length()) {
                    val txJson = transactions.getJSONObject(i)
                    val tokenParts = txJson.optString("token", "").split(":::")
                    val networkToken = tokenParts[0]
                    val tokenId = tokenParts.getOrElse(1) { "" }.split("###")[0]

                    val waitEC = txJson.optJSONArray("wait")?.let { waitArray ->
                        val waitList = mutableListOf<String>()
                        for (j in 0 until waitArray.length()) {
                            val waitObject = waitArray.optJSONObject(j)
                            if (waitObject != null) {
                                waitList.add(waitObject.optString("ecaddress", ""))
                            }
                        }
                        waitList.joinToString(",")
                    } ?: ""

                    val tx = TX(
                        unid = txJson.optString("unid", ""),
                        id = txJson.optInt("id", 0), // Парсинг ID
                        tx = txJson.optString("tx", ""), // Обработка null для tx
                        minsign = txJson.optString("min_sign", "1").toIntOrNull() ?: 1, // Парсинг min_sign
                        waitEC = waitEC, // EC Адреса подписантов, подписи которых ждёт транзакция
                        signedEC = "", // Подписи подписантов пока не обрабатываются, можно добавить аналогично waitEC при необходимости
                        network = networkToken.toIntOrNull() ?: 0, // Обработка network ID
                        token = tokenId,
                        to_addr = txJson.optString("to_addr", ""),
                        info = txJson.optString("info", ""), // Парсинг info
                        tx_value = txJson.optString("value", "0").replace(",", "").toDouble(), // Преобразование value в Double
                        value_hex = txJson.optString("value_hex", "0"), // Парсинг value_hex
                        init_ts = txJson.optLong("init_ts", 0L).toInt(), // Преобразование init_ts в Int
                        eMSG = "", // Обработка сообщения об ошибке при необходимости
                    )
                    txList.add(tx)
                }
                repository.insertAllTransactions(txList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }


    fun fetchAndStore50Transactions(context: Context) = viewModelScope.launch {
        val apiResponse = GetAPIString(context, "tx_sign_signed")
        if (apiResponse.isNotEmpty()) {
            try {
                val transactions = JSONArray(apiResponse)
                val txList = mutableListOf<TX>()
                for (i in 0 until transactions.length()) {
                    val txJson = transactions.getJSONObject(i)
                    val tokenParts = txJson.optString("token", "").split(":::")
                    val networkToken = tokenParts[0]
                    val tokenId = tokenParts.getOrElse(1) { "" }.split("###")[0]

                    val tx = TX(
                        unid = txJson.optString("unid", ""),
                        id = 0, // Assuming `id` is not provided by the response; check if needs to be parsed differently
                        tx = "", // Assuming real transaction hash ('tx') not provided
                        network = networkToken.toIntOrNull() ?: 0, // Ensure to parse the network ID correctly
                        token = tokenId,
                        to_addr = txJson.optString("to_addr", ""),
                        tx_value = txJson.optString("tx_value", "0").replace(",", "").toDouble(),
                        init_ts = txJson.optInt("init_ts", 0),
                        // Set other fields as necessary or use default values
                        minsign = 1, // Default value; adjust if the server sends this information
                        // Default values for all other fields not provided by the server
                    )
                    txList.add(tx)
                }
                repository.insertAllTransactions(txList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private val _rejectedTransactions = MutableLiveData<Map<String, String>>()
    val rejectedTransactions: LiveData<Map<String, String>> get() = _rejectedTransactions

    private val _signedTransactions = MutableLiveData<Set<String>>()
    val signedTransactions: LiveData<Set<String>> get() = _signedTransactions

    init {
        _rejectedTransactions.value = loadRejectedTransactions()
        _signedTransactions.value = loadSignedTransactions()
    }

    private fun loadRejectedTransactions(): Map<String, String> {
        val prefs = context.getSharedPreferences("TransactionPreferences", Context.MODE_PRIVATE)
        val rejectedMap = mutableMapOf<String, String>()
        prefs.getStringSet("rejectedTransactions", emptySet())?.forEach { entry ->
            val parts = entry.split("|")
            if (parts.size == 2) {
                rejectedMap[parts[0]] = parts[1]
            }
        }
        return rejectedMap
    }

    private fun loadSignedTransactions(): Set<String> {
        val prefs = context.getSharedPreferences("TransactionPreferences", Context.MODE_PRIVATE)
        return prefs.getStringSet("signedTransactions", emptySet()) ?: emptySet()
    }

    private fun saveRejectedTransactions(rejectedMap: Map<String, String>) {
        val prefs = context.getSharedPreferences("TransactionPreferences", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putStringSet("rejectedTransactions", rejectedMap.map { "${it.key}|${it.value}" }.toSet())
            apply()
        }
    }

    private fun saveSignedTransactions(signedSet: Set<String>) {
        val prefs = context.getSharedPreferences("TransactionPreferences", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putStringSet("signedTransactions", signedSet)
            apply()
        }
    }

    fun rejectTransaction(txUnid: String, reason: String) = viewModelScope.launch {
        val message = JSONObject().apply {
            put("ec_reject", reason)
        }.toString()
        val modifiedMessage = message.substring(1, message.length - 1)
        val api = "tx_reject/$txUnid"
        try {
            val response = GetAPIString(context, api, mes = modifiedMessage, POST = true)
            Log.d("TransactionRequest", "Response for rejecting transaction $txUnid: $response")
            // Update the rejection state
            _rejectedTransactions.value = _rejectedTransactions.value?.plus(txUnid to reason)
            saveRejectedTransactions(_rejectedTransactions.value ?: emptyMap())
        } catch (e: Exception) {
            Log.e("TransactionError", "Error rejecting transaction $txUnid with reason $reason", e)
        }
    }

    fun signTransaction(txUnid: String) = viewModelScope.launch {
        val api = "tx_sign/$txUnid"
        try {
            val response = GetAPIString(context, api, mes = "", POST = true)
            Log.d("TransactionRequest", "Response for signing transaction $txUnid: $response")
            // Update the signing state
            _signedTransactions.value = _signedTransactions.value?.plus(txUnid)
            saveSignedTransactions(_signedTransactions.value ?: emptySet())
        } catch (e: Exception) {
            Log.e("TransactionError", "Error signing transaction $txUnid", e)
        }
    }

    fun isTransactionRejected(transactionId: String): String? {
        return _rejectedTransactions.value?.get(transactionId)
    }

    fun isTransactionSigned(transactionId: String): Boolean {
        return _signedTransactions.value?.contains(transactionId) ?: false
    }

    var selectedWallet: MutableLiveData<Wallets> = MutableLiveData()
    var selectedToken: MutableLiveData<Balans> = MutableLiveData()

    fun selectWallet(wallet: Wallets) {
        selectedWallet.value = wallet
    }

    fun selectToken(token: Balans) {
        selectedToken.value = token
    }

    fun getBalansForTokenAddress(tokenAddr: String): LiveData<List<Balans>> = liveData {
        emit(repository.getAllBalansByAddr(tokenAddr))
    }

    fun sendTransaction(token: Balans, wallet: Wallets, address: String, amount: Double, context: Context) = viewModelScope.launch {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            decimalSeparator = '.'
        }

        val formatter = DecimalFormat("0.00", symbols)
        val formattedAmount = formatter.format(amount)

        val transactionDetails = JSONObject().apply {
            put("token", "${token.network_id}:::${token.name}###${wallet.name}")
            put("info", wallet.info)
            put("value", formattedAmount)
            put("toAddress", address)
        }


        val jsonString = transactionDetails.toString()
        val modifiedString = jsonString.substring(1, jsonString.length - 1)
        println("Transaction Details: $modifiedString")
        val rsva = Getsign(context, modifiedString)
        val requestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(context.getString(R.string.base_url) + "ece/tx")
            .addHeader("x-app-ec-from", rsva[3])
            .addHeader("x-app-ec-sign-r", rsva[0])
            .addHeader("x-app-ec-sign-s", rsva[1])
            .addHeader("x-app-ec-sign-v", rsva[2])
            .method("POST", requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                println("Transaction failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                    println("Transaction Failed")
                    return
                }

                val jsonResponse = JSONObject(responseBody)
                if (jsonResponse.has("ERROR")) {
                    println("Transaction Failed with Error")
                } else if (jsonResponse.has("tx_unid")) {
                    val transactionId = jsonResponse.getString("tx_unid")
                    println("Transaction Successful: ID $transactionId")
                    viewModelScope.launch {
                        val  tx = TX(
                            unid = transactionId,
                            tx = "", // Assuming the real transaction hash is not available yet
                            network = token.network_id,
                            token = token.name,
                            to_addr = address,
                            info = wallet.info,
                            tx_value = formattedAmount.toDouble(),
                            from = wallet.name // Assuming 'from' is the wallet name
                        )
                        repository.insertTransaction(tx)
                        println("Transaction ID saved to database successfully.")
                    }
                }
            }
        })
    }

    fun insertWallet(wallet: Wallets) = viewModelScope.launch {
        repository.insertWallet(wallet)
    }

    fun addWallets(context: Context) = viewModelScope.launch {
        val jsonString = GetAPIString(context, "wallets_2")
        val loadedWallets = parseWallets(jsonString)
        repository.addWallets(loadedWallets)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadedWallets.forEach { wallet ->
                    // Check if tokenShortNames is not blank
                    if (wallet.tokenShortNames.isNotBlank()) {
                        wallet.tokenShortNames.split(";").filter { it.isNotBlank() }.forEach { token ->
                            val parts = token.split(" ")
                            val amount = parts[0].toDoubleOrNull() ?: 0.0
                            val name = parts.getOrNull(1) ?: ""

                            repository.insertToken(Tokens(wallet.network, name, wallet.addr))
                            repository.insertBalans(Balans(name, "", wallet.addr, wallet.network, amount, 0.0))
                        }
                    }
                }
            }
        }
    }


    fun deleteWallet(wallet: Wallets) = viewModelScope.launch {
        repository.deleteWallet(wallet)
    }

    fun deleteAllWallets() = viewModelScope.launch {
        repository.deleteAllWallets()
    }

    fun getCountOfWallets(): LiveData<Int> = liveData {
        emit(repository.getCountOfWallets())
    }

    fun createWallet(context: Context,msg:String) = viewModelScope.launch {
        val jsonString = GetAPIString(context,"newWallet",msg,true)
        //в ответ получаем джейсон строку
        val jsonconversion = JSONObject(jsonString)
        // и смотрим что внутри:
        if (jsonconversion.has("ERROR")) //Всё пропало, сервер долго ругался и послал нас и наш кошелёк
            return@launch// Завершаем старания. Нужно что-то печальное юзеру сказать или значёк какой вывести..
        if (jsonconversion.has("myUNID")) // сервер успешно зарегистрировал наш запрос и у нового кошелька будет вот такой UNID. Делать нам с ним нечего.
        // Нужно запросить список кошельков с сервера и этот свежесозданный будет уже там на стадии [создаётся, ждите]
            fillWallets(context)
        // Нужно подождать пару минут и кошель появится уже и в блокчейне если всё ОК.
    }

    suspend fun fillWallets(context: Context) {
        var ss: String  = GetAPIString(context, "wallets_2")
        if (ss.isEmpty()) return;
        if (ss == "{}") ss = "[]";
        val jarr = jsonArray(ss)
        val gg = mutableListOf<Wallets>()
        for (i in 0 until jarr.length())
        {val j = jarr.getJSONObject(i)
            gg.add(Wallets(j["wallet_id"].toString().toInt(),
                j["network"].toString().toInt(),
                j.optString("myFlags", ""),
                j.optString("wallet_type","0").toInt(),
                j.optString("name",""),
                j.optString("info",""),
                j.optString("addr",""),
                j.optString("addr_info",""),
                j.optString("myUNID",""),
                j.optString("tokenShortNames","")))
        }
        repository.addWallets(gg) // allWallets обновится с триггера в базе
    }

    //Определение
    val allSigners: LiveData<List<Signer>> = repository.allSigners.asLiveData()
    val allNetworks: LiveData<List<Networks>> = repository.allNetworks.asLiveData()

    //Signer
    fun updateSigner(signer: Signer) = viewModelScope.launch{
        repository.upsertSigner(signer)
    }

    // Результат последнего сканирования QR-кода
    fun addNewSignerFromQR(result: String) {
        val newSigner = Signer(name = "Новый Подписант", address = result, email = "", type = 0, telephone = "")
        insertSigner(newSigner)
    }

    fun deleteSigner(signer: Signer) = viewModelScope.launch{
        repository.deleteSigner(signer)
    }

    fun insertSigner(signer: Signer) = viewModelScope.launch{
        repository.insertSigner(signer)
    }
    fun amountOfSigner(signer: Signer) = viewModelScope.launch{
        repository.amountOfSigner()
    }

    fun getSignerAddress(address: String): LiveData<Signer?> = liveData {
        emit(repository.getSignerAddress(address))
    }

    //Network
    fun insertNetwork(network: Networks) = viewModelScope.launch{
        repository.insertNetwork(network)
    }

    fun addNetworks(context: Context) = viewModelScope.launch{
        val jsonString = GetAPIString(context, "netlist/1")
        //val loadedNetworks = parseNetworksJsonWithGson(jsonString)
        val loadedNetworks = parseNetworks(jsonString)
        repository.addNetworks(loadedNetworks)
    }

    fun deleteNetworks() = viewModelScope.launch{
        repository.deleteNetworks()
    }

    fun deleteNetwork(network: Networks) = viewModelScope.launch{
        repository.deleteNetwork(network)
    }

    fun amountOfNetworks() = viewModelScope.launch{
        repository.amountOfNetworks()
    }

    private val _isAddingSigner = MutableLiveData<Boolean>(false)
    val isAddingSigner: LiveData<Boolean> = _isAddingSigner

    fun showAddSignerDialog() {
        _isAddingSigner.value = true
    }

    fun hideAddSignerDialog() {
        _isAddingSigner.value = false
    }

    //balans
    fun getAllBalans(): LiveData<List<Balans>> = liveData {
        emit(repository.getAllBalans())
    }

    fun getBalansCount(): LiveData<Int> = liveData {
        emit(repository.getBalansCount())
    }

    fun getOverallBalans(): LiveData<List<Balans>> = liveData {
        emit(repository.getOverallBalans())
    }

    fun getAllBalansByAddr(adr: String): LiveData<List<Balans>> = liveData {
        emit(repository.getAllBalansByAddr(adr))
    }

    fun getAllBalansByNet(net: Int): LiveData<List<Balans>> = liveData {
        emit(repository.getAllBalansByNet(net))
    }

    fun deleteBalansItem(item: Balans) = viewModelScope.launch {
        repository.deleteBalansItem(item)
    }

    fun deleteAllBalans() = viewModelScope.launch {
        repository.deleteAllBalans()
    }

    fun addBalans(items: List<Balans>) = viewModelScope.launch {
        repository.addBalans(items)
    }

    fun insertBalans(item: Balans) = viewModelScope.launch {
        repository.insertBalans(item)
    }

}

class AppViewModelFactory(private val repository: AppRepository, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(appViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return appViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}