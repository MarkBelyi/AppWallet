package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun ClickableText(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = newRoundedShape
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Light
        )
    }
}
