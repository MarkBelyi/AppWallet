package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PinDot(isFiled: Boolean) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(16.dp)
            .background(
                color = if (isFiled) colorScheme.primary else colorScheme.scrim,
                shape = CircleShape
            )
    )
}