package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BalanceRow(currency: String, balance: Double) {
    Row(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currency,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
        Text(
            text = "$balance",
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
    }
}
