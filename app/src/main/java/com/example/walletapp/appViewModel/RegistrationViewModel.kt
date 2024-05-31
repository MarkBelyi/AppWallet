package com.example.walletapp.appViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel () : ViewModel(){
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
}