package com.example.walletapp.DataBase.State

import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.ViewModel.SortType

data class SignerState(
    val signers: List<Signer> = emptyList(),
    val address: String = "",
    val name: String = "",
    val email: String = "",
    val telephone: String = "",
    val type: Int = 1,
    val isAddingSigner: Boolean = false,
    val sortType: SortType = SortType.NAME
)

