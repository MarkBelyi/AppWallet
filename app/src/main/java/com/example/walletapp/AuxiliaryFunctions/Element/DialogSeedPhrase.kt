package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun ShowWarningMnemPhraseDialog(showDialog: Boolean, onDismiss: () -> Unit, onAgree: () -> Unit) {
    var agreementText by remember { mutableStateOf("") }
    val isAgreementCorrect = remember(agreementText) {
        agreementText.equals("Согласен", ignoreCase = true) ||
                agreementText.equals("Согласна", ignoreCase = true) ||
                agreementText.equals("Yes", ignoreCase = true) ||
                agreementText.equals("Agree", ignoreCase = true) ||
                agreementText.equals("Да", ignoreCase = true)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = stringResource(id = R.string.attention),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.seed_phrase_alert_text),
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                    )
                    TextField(
                        value = agreementText,
                        onValueChange = { agreementText = it },
                        label = { Text(stringResource(id = R.string.your_answer)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isAgreementCorrect) {
                            onAgree()
                        } else {
                            onDismiss()
                        }
                    },
                    enabled = isAgreementCorrect
                ) {
                    Text(stringResource(id = R.string.agree))
                }
            },
            shape = newRoundedShape
        )
    }
}