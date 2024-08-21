package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AuxiliaryFunctions.DataClass.LanguageOption

@Composable
fun LanguageItem(
    language: LanguageOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onSelect() }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = language.displayName,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
            Text(
                text = language.englishName,
                color = colorScheme.scrim,
                fontWeight = FontWeight.Light,
                fontSize = 8.sp
            )
        }
    }
}