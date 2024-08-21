package com.example.walletapp.AuxiliaryFunctions.SealedClass

sealed class SignerModePager {
    data object History : SignerModePager()
    data object Requests : SignerModePager()
    data object Settings : SignerModePager()
}