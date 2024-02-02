package com.example.walletapp.registrationViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {
    var isPhraseSent: Boolean by mutableStateOf(false)

    var selectedTabIndex: Int by mutableStateOf(0)

    fun saveState(index: Int) {
        selectedTabIndex = index
    }
}