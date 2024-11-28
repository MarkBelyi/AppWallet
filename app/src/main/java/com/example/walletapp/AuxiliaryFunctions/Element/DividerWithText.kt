package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.paddingColumn

@Composable
fun DividerWithText(text: String, modifier: Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.onSurface)
        Text(
            text = text,
            style = typography.bodyMedium,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = paddingColumn),
            color = colorScheme.onSurface
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.onSurface)
    }
}
