package com.example.walletapp.DataBase.SignerData

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cri.wallet.database.NetworksDAO
import com.cri.wallet.database.entities.Networks
import com.example.walletapp.Server.GetAPIString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkViewModel(private val dao: NetworksDAO) : ViewModel() {
    var networks = mutableStateListOf<Networks>()
        private set

    init {
        fetchNetworks()
    }

    private fun fetchNetworks() {
        viewModelScope.launch {
            dao.fetchNets().collect { list ->
                networks.clear()
                networks.addAll(list)
            }
        }
    }

    fun getNetworksFromApiAndSave(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = GetAPIString(context, "netlist/1")
            val networksType = object : TypeToken<List<Networks>>() {}.type
            val networksList: List<Networks> = Gson().fromJson(response, networksType)
            dao.addNetworks(networksList)
            // Нет необходимости вызывать fetchNetworks здесь, так как Flow автоматически обновится
        }
    }
}