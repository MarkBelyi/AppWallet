package com.example.walletapp.elements.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import com.example.walletapp.R
import com.example.walletapp.ui.theme.roundedShape


@Composable
fun MnemonicTitleWithIcon(
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.seed_phrase_name),
            style = TextStyle(
                fontSize = typography.titleLarge.fontSize,
                color = colorScheme.onBackground
            ),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = { showDialog = true }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                tint = colorScheme.primary
            )
        }
    }

    if (showDialog) {
        SeedPhraseDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun SeedPhraseDialog(onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss()},
        //title = { Text("Мнемоническая фраза") },
        title = { Text(text = stringResource(id = R.string.seed_phrase_name))},
        /*text = { Text("Мнемоническая фраза - это 12 слов, " +
                "помогающие восстановить ваш секретный ключ в случае утери. " +
                "Воспользовавшись ею, любой человек может заполучить ваш ключ, " +
                "поэтому рекомендуется её никому не показывать и никуда не пересылать.") },*/
        text = { Text(text = stringResource(id = R.string.help1_mnemo))},
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("OK")
            }
        },
        shape = roundedShape
    )
}