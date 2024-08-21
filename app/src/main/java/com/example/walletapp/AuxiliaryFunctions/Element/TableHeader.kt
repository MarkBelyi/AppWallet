package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.token),
            fontWeight = FontWeight.Normal,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(id = R.string.total_balance),
            fontWeight = FontWeight.Normal,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontSize = 14.sp
        )
    }
}