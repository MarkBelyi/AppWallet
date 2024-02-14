package com.example.walletapp.DataBase.Event

import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.SignerData.SortType

sealed interface SignerEvent {
    object SaveSigner: SignerEvent

    data class SetName(val name: String): SignerEvent
    data class SetEmail(val email: String): SignerEvent
    data class SetTelephone(val telephone: String): SignerEvent
    data class SetType(val type: Int): SignerEvent
    data class SetAddress(val address: String): SignerEvent

    object ShowDialog: SignerEvent
    object HideDialog: SignerEvent

    data class SortSigners(val sortType: SortType): SignerEvent

    data class DeleteSigner(val signer: Signer): SignerEvent
}