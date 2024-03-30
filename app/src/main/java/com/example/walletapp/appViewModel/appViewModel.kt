package com.example.walletapp.appViewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Server.GetAPIString
import com.example.walletapp.parse.jsonArray
import com.example.walletapp.parse.parseNetworks
import com.example.walletapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class appViewModel(private val repository: AppRepository) : ViewModel() {

    // Wallets
    val allWallets: LiveData<List<Wallets>> = repository.allWallets.asLiveData()

    fun insertWallet(wallet: Wallets) = viewModelScope.launch {
        repository.insertWallet(wallet)
    }

    fun addWallets(wallets: List<Wallets>) = viewModelScope.launch {
        repository.addWallets(wallets)
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
    /*suspend fun fillWallets(context: Context) {
        var ss: String = GetAPIString(context, "wallets_2")
        if (ss.isEmpty()) return
        if (ss == "{}") ss = "[]"
        val jarr = JSONArray(ss)
        val gg = mutableListOf<Wallets>()
        for (i in 0 until jarr.length()) {
            val j = jarr.getJSONObject(i)
            gg.add(Wallets(
                j.getInt("wallet_id"),
                j.getInt("network"),
                j.optString("myFlags", ""),
                j.optInt("wallet_type", 0),
                j.optString("name", ""),
                j.optString("info", ""),
                j.optString("addr", ""),
                j.optString("addr_info", ""),
                j.optString("myUNID", ""),
                j.optString("tokenShortNames", "")
            ))
        }
        repository.addWallets(gg)

        withContext(Dispatchers.IO) {
            *//*for (wallet in gg) {
                val tokenStrings = wallet.tokenShortNames.split(";").filter { it.isNotBlank() }

                if (tokenStrings.isEmpty()) {
                    // Добавляем записи с пустыми значениями токенов и балансов для данного кошелька
                    repository.insertToken(Tokens(wallet.network, "", wallet.addr))
                    repository.insertBalans(Balans(
                        name = "",
                        contract = "",
                        addr = wallet.addr,
                        network_id = wallet.network,
                        amount = 0.0,
                        price = 0.0
                    ))
                } else {
                    for (tokenString in tokenStrings) {
                        val parts = tokenString.trim().split(" ")
                        if (parts.size >= 2) {
                            val amount = parts[0].toDoubleOrNull() ?: 0.0
                            val tokenName = parts[1]
                            // Добавляем информацию о токенах и балансах, если строка токенов не пустая
                            repository.insertToken(Tokens(wallet.network, tokenName, wallet.addr))
                            repository.insertBalans(Balans(
                                name = tokenName,
                                contract = "", // Здесь должен быть реальный адрес контракта, если известен
                                addr = wallet.addr,
                                network_id = wallet.network,
                                amount = amount,
                                price = 0.0 // Цену необходимо получить или рассчитать
                            ))
                        }
                    }
                }
            }*//*
            gg.forEach { wallet ->
                if (wallet.tokenShortNames.isBlank()) {
                    // Если список токенов пуст, добавляем пустой токен и баланс для данного кошелька
                    repository.insertToken(Tokens(wallet.network, "", wallet.addr))
                    repository.insertBalans(Balans("", "", wallet.addr, wallet.network, 0.0, 0.0))
                } else {
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
    }*/

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
        viewModelScope.launch { withContext(Dispatchers.IO) {
            for (i in gg) {
                val k = i.tokenShortNames.split(";")
                for (t in k)//0.543210029602051 CH2K;0.2 MATIC
                    repository.insertToken(Tokens(i.network, t.substringAfter(' ', ""), i.addr))
                //TODO("Балансы нужно тоже сразу распихать по базе балансов. Справишься? ")
            }}}
    }

    /*suspend fun fillWallets(context: Context) {
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
        viewModelScope.launch { withContext(Dispatchers.IO) {
            for (i in gg) {
                val k = i.tokenShortNames.split(";")
                for (t in k) {//0.543210029602051 CH2K;0.2 MATIC
                    repository.insertToken(Tokens(i.network, t.substringAfter(' ', ""), i.addr))
                    if (k.isNotEmpty()) {
                        k.forEach { tokenString ->
                            val parts = tokenString.split(" ")
                            if (parts.size >= 2) {
                                val amount = parts[0].toDoubleOrNull()
                                    ?: 0.0 // В случае ошибки парсинга ставим 0
                                val name = parts[1]

                                val balans = Balans(
                                    name = name,
                                    contract = "", //адрес контракта, его нужно будет здесь указать
                                    addr = i.addr,
                                    network_id = i.network,
                                    amount = amount,
                                    price = 0.0 // Логика для получения текущей цены токена
                                )
                                repository.insertBalans(balans)
                            }
                        }
                    } else {
                        // Создаем пустой объект Balans, если tokenShortNames была пустая
                        val balans = Balans(
                            name = "", // Пустое имя токена
                            contract = "", // Пустой адрес контракта
                            addr = i.addr,
                            network_id = i.network,
                            amount = 0.0, // Пустое количество токенов
                            price = 0.0 // Пустая цена
                        )
                        repository.insertBalans(balans)
                    }
                }
            }
        }}
    }*/

    /*suspend fun fillWallets(context: Context) {
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
        viewModelScope.launch { withContext(Dispatchers.IO) {
            for (i in gg) {
                val k = i.tokenShortNames.split(";")
                for (t in k) {//0.543210029602051 CH2K;0.2 MATIC
                    repository.insertToken(Tokens(i.network, t.substringAfter(' ', ""), i.addr))
                    TODO(Балансы тоже нужно распихать по базе)
            }
        }}
    }*/

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

    // Balans methods
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

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(appViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return appViewModel(repository) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel class")
    }
}