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
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.Server.Getsign
import com.example.walletapp.parse.jsonArray
import com.example.walletapp.parse.parseNetworks
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

    //SharedPreferences
    private val _selectedAuthMethod = MutableLiveData<AuthMethod>()
    private val selectedAuthMethod: LiveData<AuthMethod> = _selectedAuthMethod

    fun updateAuthMethod(authMethod: AuthMethod, context: Context) {
        val prefs = context.getSharedPreferences("AuthPreferences", Context.MODE_PRIVATE)
        prefs.edit().putString("AuthMethod", authMethod.name).apply()
        _selectedAuthMethod.value = authMethod
    }

    fun getAuthMethod(): LiveData<AuthMethod> = selectedAuthMethod

    //Показывать Тестовые сети

    private val _showTestNetworks = MutableLiveData<Boolean>()
    val showTestNetworks: LiveData<Boolean> get() = _showTestNetworks

    private fun loadShowTestNetworksPreference() {
        val prefs = context.getSharedPreferences("WalletPreferences", Context.MODE_PRIVATE)
        _showTestNetworks.value = prefs.getBoolean("showTestNetworks", false)
        filterWalletsBasedOnPreference()
    }

    fun saveShowTestNetworksPreference(show: Boolean) {
        val prefs = context.getSharedPreferences("WalletPreferences", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("showTestNetworks", show).apply()
        _showTestNetworks.value = show
        filterWalletsBasedOnPreference()
    }

    private fun filterWalletsBasedOnPreference() {
        if (_showTestNetworks.value == true) {
            getAllWallets()
        } else {
            getVisibleWallets()
        }
    }


    // Wallets and Tx
    val allWallets: LiveData<List<Wallets>> = repository.allWallets.asLiveData()
    private val _filteredWallets = MutableLiveData<List<Wallets>>()
    val filteredWallets: LiveData<List<Wallets>> get() = _filteredWallets

    init {
        viewModelScope.launch {
            _filteredWallets.value = repository.fetchAllWallets()
        }
    }

    fun filterWalletsByNetwork(network: Int, testNetwork: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredWallets.postValue(repository.getWalletsByNetwork(network, testNetwork))
        }
    }

    fun filterWalletsByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredWallets.postValue(repository.getWalletsByName(name))
        }
    }

    fun getAllWallets() {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredWallets.postValue(repository.fetchAllWallets())
        }
    }

    //TX
    val allTX: LiveData<List<TX>> = repository.allTX.asLiveData()

    // Method to update transaction status
    private fun updateTransactionStatus(unid: String, status: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTransactionStatus(unid, status)
        }
    }

    // Method to fetch transactions and update their status based on server response
    fun needSignTX(context: Context) = viewModelScope.launch(Dispatchers.IO) {
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

                    val status = if (waitEC.contains(GetMyAddr(context))) 1 else 5

                    val tx = TX(
                        unid = txJson.optString("unid", ""),
                        id = txJson.optInt("id", 0),
                        tx = txJson.optString("tx", ""),
                        minsign = txJson.optString("min_sign", "1").toIntOrNull() ?: 1,
                        waitEC = waitEC,
                        signedEC = "",
                        network = networkToken.toIntOrNull() ?: 0,
                        token = tokenId,
                        to_addr = txJson.optString("to_addr", ""),
                        info = txJson.optString("info", ""),
                        tx_value = txJson.optString("value", "0").replace(",", "").toDouble(),
                        value_hex = txJson.optString("value_hex", "0"),
                        init_ts = txJson.optLong("init_ts", 0L).toInt(),
                        eMSG = "",
                        status = status
                    )
                    txList.add(tx)
                }
                repository.insertAllTransactions(txList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun signTransaction(txUnid: String) = viewModelScope.launch(Dispatchers.IO) {
        val api = "tx_sign/$txUnid"
        try {
            val response = GetAPIString(context, api, mes = "", POST = true)
            Log.d("TransactionRequest", "Response for signing transaction $txUnid: $response")
            withContext(Dispatchers.Main) {
                updateTransactionStatus(txUnid, 2)
            }
        } catch (e: Exception) {
            Log.e("TransactionError", "Error signing transaction $txUnid", e)
        }
    }

    fun rejectTransaction(txUnid: String, reason: String) = viewModelScope.launch(Dispatchers.IO) {
        val message = JSONObject().apply {
            put("ec_reject", reason)
        }.toString()
        val modifiedMessage = message.substring(1, message.length - 1)
        val api = "tx_reject/$txUnid"
        try {
            val response = GetAPIString(context, api, mes = modifiedMessage, POST = true)
            Log.d("TransactionRequest", "Response for rejecting transaction $txUnid: $response")
            withContext(Dispatchers.Main) {
                updateTransactionStatus(txUnid, 3)
            }
        } catch (e: Exception) {
            Log.e("TransactionError", "Error rejecting transaction $txUnid with reason $reason", e)
        }
    }

    fun fetchAndStoreTransactions(context: Context) = viewModelScope.launch(Dispatchers.IO) {
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

    fun sendTransaction(token: Balans, wallet: Wallets, address: String, amount: Double, info: String, context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            decimalSeparator = '.'
        }

        val formatter = DecimalFormat("0.00", symbols)
        val formattedAmount = formatter.format(amount)

        val transactionDetails = JSONObject().apply {
            put("token", "${token.network_id}:::${token.name}###${wallet.name}")
            put("info", info)
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
                    viewModelScope.launch(Dispatchers.IO) {
                        val tx = TX(
                            unid = transactionId,
                            tx = "",
                            network = token.network_id,
                            token = token.name,
                            to_addr = address,
                            info = wallet.info,
                            tx_value = formattedAmount.toDouble(),
                            from = wallet.name
                        )
                        repository.insertTransaction(tx)
                        println("Transaction ID saved to database successfully.")
                    }
                }
            }
        })
    }

    fun addWallets(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val jsonString = GetAPIString(context, "wallets_2")
        val loadedWallets = parseWallets(jsonString)
        repository.addWallets(loadedWallets)
        withContext(Dispatchers.Main) {
            _filteredWallets.value = repository.fetchAllWallets()
        }
        loadedWallets.forEach { wallet ->
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

    fun refreshWallets(context: Context, onComplete: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val jsonString = GetAPIString(context, "wallets_2")
            val loadedWallets = parseWallets(jsonString)
            repository.addWallets(loadedWallets)
            withContext(Dispatchers.Main) {
                _filteredWallets.value = repository.fetchAllWallets()
                onComplete()
            }
            loadedWallets.forEach { wallet ->
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
        } catch (e: Exception) {
            Log.e("RefreshWallets", "Error refreshing wallets", e)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun createWallet(context: Context, msg: String) = viewModelScope.launch(Dispatchers.IO) {
        val jsonString = GetAPIString(context, "newWallet", msg, true)
        val jsonconversion = JSONObject(jsonString)
        if (jsonconversion.has("ERROR")) return@launch
        if (jsonconversion.has("myUNID")) fillWallets(context)
    }

    suspend fun fillWallets(context: Context) {
        var ss: String = GetAPIString(context, "wallets_2")
        if (ss.isEmpty()) return
        if (ss == "{}") ss = "[]"
        val jarr = jsonArray(ss)
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
                    j.optString("tokenShortNames", "")
                )
            )
        }
        repository.addWallets(gg)
    }

    fun updateWalletFlags(unid: String, newFlags: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = visibilityWallet(context, unid, newFlags)
                if (response.isEmpty()) {
                    repository.updateWalletFlags(unid, newFlags)
                    Log.d("UpdateWalletFlags", "Wallet flags updated locally: $newFlags")
                } else {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.has("ERROR")) {
                        val error = jsonResponse.getString("ERROR")
                        Log.e("UpdateWalletFlags", "Failed to update wallet flags: $error")
                    } else {
                        Log.d("UpdateWalletFlags", "Wallet flags updated successfully: $response")
                    }
                }
            } catch (e: IOException) {
                Log.e("UpdateWalletFlags", "Network error while updating wallet flags", e)
            } catch (e: JSONException) {
                Log.e("UpdateWalletFlags", "JSON error while updating wallet flags", e)
            } catch (e: Exception) {
                Log.e("UpdateWalletFlags", "Error updating wallet flags", e)
            }
        }
    }

    suspend fun visibilityWallet(context: Context, unid: String, newFlags: String): String {
        val apiEndpoint = "set_wallet_flag/$unid"
        val requestBody = "\"wallet_flags\":\"" + newFlags + "\""
        return GetAPIString(context, apiEndpoint, requestBody, POST = true)
    }

    fun getVisibleWallets() {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredWallets.postValue(repository.getVisibleWallets())
        }
    }

    fun getOnlyHiddenWallets() {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredWallets.postValue(repository.getOnlyHiddenWallets())
        }
    }

    // Signer
    val allSigners: LiveData<List<Signer>> = repository.allSigners.asLiveData()

    fun addSigner(signersKeys: MutableList<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                signersKeys.add("")
            }
        }
    }

    fun removeSigner(index: Int, signersKeys: MutableList<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                signersKeys.removeAt(index)
            }
        }
    }

    fun updateSigner(signer: Signer) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsertSigner(signer)
    }

    fun addNewSignerFromQR(result: String) {
        val newSigner = Signer(name = "Новый Подписант", address = result, email = "", type = 0, telephone = "")
        insertSigner(newSigner)
    }

    fun deleteSigner(signer: Signer) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSigner(signer)
    }

    fun insertSigner(signer: Signer) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSigner(signer)
    }

    fun getSignerAddress(address: String): LiveData<Signer?> = liveData {
        emit(repository.getSignerAddress(address))
    }

    // Network
    val allNetworks: LiveData<List<Networks>> = repository.allNetworks.asLiveData()

    fun addNetworks(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val jsonString = GetAPIString(context, "netlist/1")
        val loadedNetworks = parseNetworks(jsonString)
        repository.addNetworks(loadedNetworks)
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


