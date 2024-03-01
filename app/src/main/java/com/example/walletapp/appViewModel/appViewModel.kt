package com.example.walletapp.appViewModel

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.Server.GetAPIString
import com.example.walletapp.parse.parseNetworks
import com.example.walletapp.parse.parseNetworksJsonWithGson
import com.example.walletapp.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class appViewModel(private val repository: AppRepository) : ViewModel() {

    val allSigners: LiveData<List<Signer>> = repository.allSigners.asLiveData()
    val allNetworks: LiveData<List<Networks>> = repository.allNetworks.asLiveData()

    //Signer
    fun upsertSigner(signer: Signer) = viewModelScope.launch{
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