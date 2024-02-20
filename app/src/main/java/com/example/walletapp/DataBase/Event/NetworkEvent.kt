package com.example.walletapp.DataBase.Event

sealed interface NetworkEvent {
    object GetNetwork: NetworkEvent

}