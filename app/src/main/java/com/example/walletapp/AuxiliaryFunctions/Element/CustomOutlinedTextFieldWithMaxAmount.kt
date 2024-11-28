package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun CustomOutlinedTextFieldWithMaxAmount(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxAmount: String
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        },
        singleLine = true,
        shape = newRoundedShape,
        colors = TextFieldDefaults.colors(
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            TextButton(onClick = { onValueChange(maxAmount) }) {
                Text(
                    text = "MAX",
                    fontSize = 12.sp,
                    color = colorScheme.primary
                )
            }
        },
        maxLines = 1,
    )
}