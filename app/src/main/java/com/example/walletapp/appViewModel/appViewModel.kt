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
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Server.GetAPIString
import com.example.walletapp.parse.parseNetworks
import com.example.walletapp.repository.AppRepository
import kotlinx.coroutines.launch

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

    //Регистрация
    var isPhraseSent: Boolean by mutableStateOf(false)

    var selectedTabIndex: Int by mutableStateOf(0)

    fun saveState(index: Int) {
        selectedTabIndex = index
    }

    private val _mnemonicList = MutableLiveData<List<String>>()

    private val _mnemonic = MutableLiveData<String>()

    fun setMnemonic(mnemonic: String) {
        _mnemonic.value = mnemonic
    }
    fun setMnemonicList(list: List<String>) {
        _mnemonicList.value = list
    }

    fun getMnemonic(): String {
        return _mnemonic.value ?: ""
    }

    fun getMnemonicList(): List<String> {
        // Возвращаем текущее значение _mnemonicList, если оно не null, иначе возвращаем пустой список
        return _mnemonicList.value ?: emptyList()
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