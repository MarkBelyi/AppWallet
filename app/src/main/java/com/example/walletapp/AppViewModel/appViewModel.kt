package com.example.walletapp.AppViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.walletapp.AuxiliaryFunctions.DataClass.Blockchain
import com.example.walletapp.AuxiliaryFunctions.ENUM.AuthMethod
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.DataBase.Entities.AllTX
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.WalletAddress
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Parsing.jsonArray
import com.example.walletapp.Parsing.parseNetworks
import com.example.walletapp.Parsing.parseTokensInfo
import com.example.walletapp.Parsing.parseWallets
import com.example.walletapp.R
import com.example.walletapp.Repository.AppRepository
import com.example.walletapp.Server.GetAPIString
import com.example.walletapp.Server.Getsign
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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


//TODO(Исправить ошибки, сделать везде try catch)
class appViewModel(private val repository: AppRepository, private val application: Application) :
    AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val sharedPreferences =
        application.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    //QR
    private val _qrResult = MutableLiveData<String?>()
    val qrResult: LiveData<String?> get() = _qrResult

    fun setQrResult(result: String?) {
        _qrResult.value = result
    }

    fun clearQrResult() {
        _qrResult.value = null
    }

    //SharedPreferences - Theme
    private val _isDarkTheme = MutableLiveData(getInitialThemePreference())
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    private fun getInitialThemePreference(): Boolean {
        val savedPreference = sharedPreferences.getBoolean("is_dark_theme", false)
        return if (sharedPreferences.contains("is_dark_theme")) {
            savedPreference
        } else {
            val currentNightMode =
                application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }
        }
    }

    private fun saveThemePreference(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }

    fun toggleTheme() {
        val newTheme = _isDarkTheme.value?.not() ?: false
        _isDarkTheme.value = newTheme
        saveThemePreference(newTheme)
    }

    // SharedPreferences - Language
    private val _isEnglishLanguage = MutableLiveData(getSavedLanguagePreference())
    val isEnglishLanguage: LiveData<Boolean> get() = _isEnglishLanguage

    private fun getSavedLanguagePreference(): Boolean {
        return sharedPreferences.getBoolean("is_english_language", isSystemLanguageEnglish())
    }

    private fun saveLanguagePreference(isEnglishLanguage: Boolean) {
        sharedPreferences.edit().putBoolean("is_english_language", isEnglishLanguage).apply()
    }

    internal fun isSystemLanguageEnglish(): Boolean {
        val currentLocale = Locale.getDefault().language
        return currentLocale == "en"
    }

    fun toggleLanguage() {
        val newLanguage = _isEnglishLanguage.value?.not() ?: true
        _isEnglishLanguage.value = newLanguage
        saveLanguagePreference(newLanguage)
        updateLocale(newLanguage)
    }

    private fun updateLocale(isEnglishLanguage: Boolean) {
        val locale = if (isEnglishLanguage) {
            Locale("en")
        } else {
            Locale("ru")
        }
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration

        config.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        config.setLocales(localeList)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    //AuthMethod
    private val _selectedAuthMethod = MutableLiveData<AuthMethod>()
    private val selectedAuthMethod: LiveData<AuthMethod> = _selectedAuthMethod

    init {
        _selectedAuthMethod.value = getAuthMethodFromPrefs()
    }

    fun getAuthMethod(): LiveData<AuthMethod> = selectedAuthMethod

    fun setAuthMethod(authMethod: AuthMethod) {
        _selectedAuthMethod.value = authMethod
        saveAuthMethodToPrefs(authMethod)
    }

    private fun saveAuthMethodToPrefs(authMethod: AuthMethod) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(authMethod)
        editor.putString("auth_method", json)
        editor.apply()
    }

    private fun getAuthMethodFromPrefs(): AuthMethod {
        val json = sharedPreferences.getString("auth_method", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<AuthMethod>() {}.type)
        } else {
            AuthMethod.PASSWORD
        }
    }

    private val _showTestNetworks = MutableLiveData<Boolean>()

    init {
        _showTestNetworks.value = sharedPreferences.getBoolean("show_test_networks", false)
    }

    fun updateShowTestNetworks(show: Boolean) {
        sharedPreferences.edit().putBoolean("show_test_networks", show).apply()
        _showTestNetworks.value = show
        refreshNetworks()
    }

    private val _networks = MutableLiveData<List<Networks>>()
    val networks: LiveData<List<Networks>> get() = _networks

    fun refreshNetworks() {
        viewModelScope.launch {
            val showTestNetworks = _showTestNetworks.value ?: false
            val networks = if (showTestNetworks) {
                repository.getMainWithTestNetworks().first()
            } else {
                repository.getMainNetworks().first()
            }
            _networks.value = networks
        }
    }

    init {
        refreshNetworks()
    }

    //Показывать Кошельки с тестовыми сетями
    private val _showWalletWithTestNetwork = MutableLiveData(false)
    val showWalletWithTestNetwork: LiveData<Boolean> get() = _showWalletWithTestNetwork

    // Wallets and Tx
    private val _filteredWallets = MutableLiveData<List<Wallets>>()
    val filteredWallets: LiveData<List<Wallets>> get() = _filteredWallets
    private val _selectedBlockchain = MutableLiveData<Blockchain?>()
    val selectedBlockchain: LiveData<Blockchain?> get() = _selectedBlockchain

    fun filterWallets() {
        val blockchainId = _selectedBlockchain.value?.id
        val showHidden = _showWalletWithTestNetwork.value ?: false

        viewModelScope.launch(Dispatchers.IO) {
            val wallets = if (blockchainId != null) {
                repository.getWalletsByNetwork(blockchainId, getTestNetworkId(blockchainId))
            } else {
                repository.fetchAllWallets()
            }

            val filtered = if (showHidden) {
                wallets
            } else {
                wallets.filter { !it.myFlags.startsWith("1") }
            }

            _filteredWallets.postValue(filtered)
        }
    }

    fun updateSelectedBlockchain(blockchain: Blockchain?) {
        _selectedBlockchain.value = blockchain
        filterWallets()
    }

    fun toggleShowHidden() {
        _showWalletWithTestNetwork.value = !(_showWalletWithTestNetwork.value ?: false)
        filterWallets()
    }

    private fun getTestNetworkId(networkId: Int): Int {
        return when (networkId) {
            1000 -> 1010
            3000 -> 3040
            5000 -> 5010
            else -> networkId
        }
    }

    fun filterWalletsByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredWallets.postValue(repository.getWalletsByName(name))
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

    private fun updateTransactionRejectReason(unid: String, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTransactionRejectReason(unid, reason)
        }
    }

    fun createNewWallet(
        context: Context,
        signerKeys: List<String>,
        requiredSigners: Int,
        selectedNetworkId: String,
        walletNameText: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val ec = signerKeys
                .filter { it.isNotEmpty() }
                .toList()

            var ss = ""
            ss = "\"slist\":{"
            for (i in ec.indices) {
                ss += "\"$i\":{\"type\":\"any\",\"ecaddress\":\"${ec[i]}\"}"
                if (i < ec.size - 1) ss += ","
            }

            if (requiredSigners > 0)
                ss += ",\"min_signs\":\"$requiredSigners\""
            ss += "},"
            ss += "\"network\":\"$selectedNetworkId\","
            ss += "\"info\":\"$walletNameText\""

            createWallet(context, ss)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun needSignTX(context: Context, onComplete: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val apiResponse = GetAPIString(context, "tx_by_ec")
            withContext(Dispatchers.Main) {
                onComplete()
            }
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

                        val signedEC = txJson.optJSONArray("signed")?.let { signedArray ->
                            val signedList = mutableListOf<String>()
                            for (j in 0 until signedArray.length()) {
                                val signedObject = signedArray.optJSONObject(j)
                                if (signedObject != null) {
                                    signedList.add(signedObject.optString("ecaddress", ""))
                                }
                            }
                            signedList.joinToString(",")
                        } ?: ""

                        val tx = TX(
                            unid = txJson.optString("unid", ""),
                            id = txJson.optInt("id", 0),
                            tx = txJson.optString("tx", ""),
                            minsign = txJson.optString("min_sign", "1").toIntOrNull() ?: 1,
                            waitEC = waitEC,
                            signedEC = signedEC,
                            network = networkToken.toIntOrNull() ?: 0,
                            token = tokenId,
                            to_addr = txJson.optString("to_addr", ""),
                            info = txJson.optString("info", ""),
                            tx_value = txJson.optString("tx_value", "0").replace(",", "")
                                .toDouble(),
                            init_ts = txJson.optLong("init_ts", 0L).toInt(),
                            eMSG = "",
                            status = 0
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
                updateTransactionRejectReason(txUnid, reason)
            }
        } catch (e: Exception) {
            Log.e("TransactionError", "Error rejecting transaction $txUnid with reason $reason", e)
        }
    }

    var selectedWallet: MutableLiveData<Wallets> = MutableLiveData()
    var selectedToken: MutableLiveData<Balans> = MutableLiveData()

    fun getCombinedBalances(): LiveData<Map<String, Double>> = liveData {
        val balance = sharedPreferences.getBoolean("show_test_networks", false)
        val combinedBalances = if (balance) {
            repository.getCombinedBalancesWithTest()
        } else repository.getCombinedBalances()

        val balancesMap = mutableMapOf<String, Double>()

        combinedBalances.forEach { networkBalance ->
            val tokenName = networkBalance.name
            balancesMap[tokenName] = (balancesMap[tokenName] ?: 0.0) + networkBalance.totalAmount
        }

        emit(balancesMap)
    }

    fun selectWallet(wallet: Wallets) {
        selectedWallet.value = wallet
    }

    fun selectToken(token: Balans) {
        selectedToken.value = token
    }

    fun getBalansForTokenAddress(tokenAddr: String): LiveData<List<Balans>> = liveData {
        emit(repository.getAllBalansByAddr(tokenAddr))
    }

    fun sendTransaction(
        token: Balans,
        wallet: Wallets,
        address: String,
        amount: Double,
        info: String,
        context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
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
        val requestBody =
            jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
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
                        println("Transaction ID saved to database successfully.")
                    }
                }
            }
        })
    }

    fun sendTransactionWithAutoExchange(
        token: Balans,
        wallet: Wallets,
        address: String,
        amount: Double,
        info: String,
        context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            decimalSeparator = '.'
        }

        val formatter = DecimalFormat("0.00", symbols)
        val formattedAmount = formatter.format(amount)

        val transactionDetails = JSONObject().apply {
            put("autoExchange", "0")
            put("token", "${token.network_id}:::${token.name}###${wallet.name}")
            put("info", info)
            put("value", formattedAmount)
            put("toAddress", address)
        }

        val jsonString = transactionDetails.toString()
        val modifiedString = jsonString.substring(1, jsonString.length - 1)
        println("Transaction Details: $modifiedString")
        val rsva = Getsign(context, modifiedString)
        val requestBody =
            jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
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
                        println("Transaction ID saved to database successfully.")
                    }
                }
            }
        })
    }

    suspend fun getTrxBalance(walletAddress: String): Double {

        if (walletAddress.isBlank()) {
            return 0.0
        }

        val wallet = repository.getWalletByAddress(walletAddress)
        return wallet?.let {
            it.tokenShortNames.split(";")
                .firstOrNull { token -> token.contains("TRX") }
                ?.split(" ")
                ?.firstOrNull()
                ?.toDoubleOrNull() ?: 0.0
        } ?: 0.0

    }

    fun refreshWallets(context: Context, onComplete: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = GetAPIString(context, "wallets_2")
                val loadedWallets = parseWallets(jsonString)
                repository.addWallets(loadedWallets)

                withContext(Dispatchers.Main) {
                    filterWallets()
                    onComplete()
                }

                loadedWallets.forEach { wallet ->
                    if (wallet.tokenShortNames.isNotBlank()) {
                        wallet.tokenShortNames.split(";").filter { it.isNotBlank() }
                            .forEach { token ->
                                val parts = token.split(" ")
                                val amount = parts[0].toDoubleOrNull() ?: 0.0
                                val name = parts.getOrNull(1) ?: ""

                                repository.insertToken(Tokens(wallet.network, name, wallet.addr))
                                repository.insertBalans(
                                    Balans(
                                        name,
                                        "",
                                        wallet.addr,
                                        wallet.network,
                                        amount,
                                        0.0
                                    )
                                )
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


    private fun createWallet(context: Context, msg: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val jsonString = GetAPIString(context, "newWallet", msg, true)
            val jsonconversion = JSONObject(jsonString)
            if (jsonconversion.has("ERROR")) return@launch
            if (jsonconversion.has("myUNID")) fillWallets(context)
        }

    private suspend fun fillWallets(context: Context) {
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

    private val _chooseWallet = MutableLiveData<Wallets?>()
    val chooseWallet: LiveData<Wallets?> get() = _chooseWallet

    fun chooseWallet(wallet: Wallets?) {
        _chooseWallet.value = wallet
    }

    //Overloading function
    fun updateWalletFlags(unid: String, newFlags: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWalletFlags(unid, newFlags)
            visibilityWallet(getApplication(), unid, newFlags)
            refreshFilteredWallets()
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    private suspend fun refreshFilteredWallets() {
        var wallets: List<Wallets>
        val blockchainId = _selectedBlockchain.value?.id
        val showHidden = _showWalletWithTestNetwork.value ?: false

        withContext(Dispatchers.IO) {
            wallets = if (blockchainId != null) {
                repository.getWalletsByNetwork(blockchainId, getTestNetworkId(blockchainId))
            } else {
                repository.fetchAllWallets()
            }

            val filtered = if (showHidden) {
                wallets
            } else {
                wallets.filter { !it.myFlags.startsWith("1") }
            }

            _filteredWallets.postValue(filtered)
        }
    }

    private suspend fun visibilityWallet(context: Context, unid: String, newFlags: String): String {
        val apiEndpoint = "set_wallet_flag/$unid"
        val requestBody = "\"wallet_flags\":\"$newFlags\""
        return GetAPIString(context, apiEndpoint, requestBody, POST = true)
    }

    // Signer
    val allSigners: LiveData<List<Signer>> = repository.allSigners.asLiveData()

    fun removeSigner(index: Int, signersKeys: MutableList<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                signersKeys.removeAt(index)
            }
        }
    }

    fun updateSigner(signer: Signer) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsertSigner(signer)
    }

    fun addNewSignerFromQR(result: String) {
        val newSigner = Signer(
            name = "Новый Подписант",
            address = result,
            email = "",
            type = 0,
            telephone = "",
            isFavorite = false
        )
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
    fun addNetworks(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val jsonString = GetAPIString(context, "netlist/1")
        val loadedNetworks = parseNetworks(jsonString)
        repository.addNetworks(loadedNetworks)
    }

    //Tokens
    private val _tokens = MutableLiveData<List<Tokens>>()
    val tokens: LiveData<List<Tokens>> get() = _tokens

    fun getTokensInfoComission() = viewModelScope.launch(Dispatchers.IO) {
        val s = GetAPIString(context, "tokensinfo")

        // Преобразуем ответ в список объектов
        val tokensList = parseTokensInfo(s)

        // Пройдемся по каждому токену и обновим его в базе данных
        tokensList.forEach { tokenInfo ->
            val networkId = tokenInfo.token.split(":::")[0].toInt()
            val name = tokenInfo.token.split(":::")[1]

            val existingToken = repository.getToken(networkId, name)

            if (existingToken != null) {
                // Обновляем только нужные поля
                repository.updateTokenCommissions(
                    networkId = networkId,
                    name = name,
                    c = tokenInfo.c.toFloat(),
                    cMin = tokenInfo.cMin.toFloat(),
                    cMax = tokenInfo.cMax.toFloat(),
                    cBase = tokenInfo.cBase.toFloat()
                )
            }
        }
    }

    private val _tokenWithCommission = MutableLiveData<Tokens?>()
    val tokenWithCommission: LiveData<Tokens?> get() = _tokenWithCommission

    fun getTokenCommission(networkId: Int, name: String, addr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Получаем токен из базы данных
                val token = repository.getToken(networkId, name, addr)
                // Обновляем LiveData с полученными данными
                _tokenWithCommission.postValue(token)
            } catch (e: Exception) {
                // Обрабатываем ошибку, если необходимо (например, логирование)
                Log.e("appViewModel", "Error fetching token commission", e)
            }
        }
    }

    //AllTX
    val allUserTX: LiveData<List<AllTX>> = repository.allUserTX.asLiveData()
    private val _filteredTransactions = MutableLiveData<List<AllTX>>()
    val filteredTransactions: LiveData<List<AllTX>> get() = _filteredTransactions

    fun filterTX(name: String, token: String, completedOnly: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            // Получаем все транзакции из репозитория
            val allTransactions = repository.getAllTransactions().firstOrNull() ?: emptyList()

            // Применяем фильтр по имени, если строка поиска не пуста
            val filteredByName = if (name.isNotEmpty()) {
                allTransactions.filter { tx -> tx.info.contains(name, ignoreCase = true) }
            } else {
                allTransactions
            }

            // Применяем фильтр по токену, если выбран конкретный токен
            val filteredByToken = if (token != "Все токены") {
                filteredByName.filter { tx -> tx.token == token }
            } else {
                filteredByName
            }

            // Применяем фильтр по завершенности, если он включен
            val finalFiltered = if (completedOnly) {
                filteredByToken.filter { tx -> tx.tx.isNotEmpty() && tx.tx != "null" }
            } else {
                filteredByToken
            }

            // Обновляем данные для отображения
            _filteredTransactions.postValue(finalFiltered)
        }
    }


    fun fetchAndStoreTransactions(context: Context, onComplete: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val apiResponse = GetAPIString(context, "tx")
            if (apiResponse.isNotEmpty()) {
                try {
                    val transactions = JSONArray(apiResponse)
                    val txList = mutableListOf<AllTX>()
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

                        // Обработка параметра "signed"
                        val signedEC = txJson.optJSONArray("signed")?.let { signedArray ->
                            val signedList = mutableListOf<String>()
                            for (j in 0 until signedArray.length()) {
                                val signedObject = signedArray.optJSONObject(j)
                                if (signedObject != null) {
                                    val ecAddress = signedObject.optString("ecaddress", "")
                                    val ecSign = signedObject.optString("ecsign", "")
                                    signedList.add("$ecAddress:$ecSign")
                                }
                            }
                            signedList.joinToString(",")
                        } ?: ""


                        val tx = AllTX(
                            unid = txJson.optString("unid", ""),
                            id = txJson.optInt("id", 0), // Парсинг ID
                            tx = txJson.optString("tx", ""), // Обработка null для tx
                            minsign = txJson.optString("min_sign", "1").toIntOrNull()
                                ?: 1, // Парсинг min_sign
                            init_ts = txJson.optLong("init_ts", 0L)
                                .toInt(), // Преобразование init_ts в Int
                            tx_value = txJson.optString("value", "0").replace(",", "")
                                .toDouble(), // Преобразование value в Double
                            value_hex = txJson.optString("value_hex", "0"), // Парсинг value_hex
                            to_addr = txJson.optString("to_addr", ""),
                            token = tokenId,
                            network = networkToken.toIntOrNull() ?: 0, // Обработка network ID
                            info = txJson.optString("info", ""), // Парсинг info

                            waitEC = waitEC, // EC Адреса подписантов, подписи которых ждёт транзакция
                            signedEC = signedEC, // Подписи подписантов пока не обрабатываются, можно добавить аналогично waitEC при необходимости
                            eMSG = "",
                        )
                        txList.add(tx)
                    }
                    repository.insertAllUserTransactions(txList)
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

    //WalletAddress

    val allWalletAddresses: LiveData<List<WalletAddress>> =
        repository.allWalletAddresses.asLiveData()

    fun deleteWalletAddress(item: WalletAddress) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteWalletAddress(item)
    }

    fun insertWalletAddress(item: WalletAddress) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertWalletAddress(item)
    }

    fun addNewAddressFromQR(result: String) {
        val newAddress = WalletAddress(
            ownerName = R.string.new_wallet_address.toString(),
            address = result,
            blockchain = "",
            token = ""
        )
        insertWalletAddress(newAddress)
    }

    //DataBase
    fun clearDataBase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearDataBase()
        }
    }

    fun deleteMyAccount(reason: String) = viewModelScope.launch(Dispatchers.IO) {
        var s: String = GetAPIString(con = context, api = "uuid")
        val json = JSONObject(s)
        if (json.has("uuid")) s = json["uuid"].toString()
        val body = "\"uuid\":\"$s\",\"reason\":\"$reason\""
        val status = GetAPIString(con = context, api = "erase_account\\$s", mes = body, POST = true)
        println(status)

        // Предположим, что статус успешного удаления аккаунта возвращает "DONE"
        if (status.contains("\"STATUS\":\"DONE\"")) { // Проверяем успешное удаление
            // Удаляем ключи и очищаем SharedPreferences
            val ps = PasswordStorageHelper(context)
            ps.remove("MyPublicKey")
            ps.remove("MyPrivateKey")
            context.getSharedPreferences(context.getString(R.string.preferens_file_name), 0).edit()
                .clear().apply()
        }
    }

}

class AppViewModelFactory(
    private val repository: AppRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(appViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return appViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


