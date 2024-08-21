package com.example.walletapp.AuxiliaryFunctions.DataClass

import com.example.walletapp.AuxiliaryFunctions.ENUM.ElementType

data class Element(
    val name: String,
    val description: String,
    val type: ElementType,
    val prefsKey: String
)