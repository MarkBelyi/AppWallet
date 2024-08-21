package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun PinDots(pinNumber: String) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Row {
            for (i in 1..4) {
                PinDot(isFiled = pinNumber.length >= i)
            }
        }
    }
}