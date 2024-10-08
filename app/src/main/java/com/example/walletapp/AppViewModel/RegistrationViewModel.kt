package com.example.walletapp.AppViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel() : ViewModel() {
    var isPhraseSent: Boolean by mutableStateOf(false)

    var selectedTabIndex: Int by mutableIntStateOf(0)

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
        return _mnemonicList.value ?: emptyList()
    }
}