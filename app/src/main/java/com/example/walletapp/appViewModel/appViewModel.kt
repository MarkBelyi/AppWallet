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
                TODO("Балансы нужно тоже сразу распихать по базе балансов. Справишься? ")
            }}
        }
    }

    //Определение
    val allSigners: LiveData<List<Signer>> = repository.allSigners.asLiveData()
    val allNetworks: LiveData<List<Networks>> = repository.allNetworks.asLiveData()

    //Signer
    fun updateSigner(signer: Signer) = viewModelScope.launch{
        repository.upsertSigner(signer)
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