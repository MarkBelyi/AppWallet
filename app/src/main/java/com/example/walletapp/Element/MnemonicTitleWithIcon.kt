package com.example.walletapp.Element

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape


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
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = { showDialog = true }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                tint = colorScheme.onSurfaceVariant
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
        title = { Text(
            text = stringResource(id = R.string.seed_phrase_name),
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        ) },
        text = { Text(
            text = stringResource(id = R.string.help1_mnemo),
            fontWeight = FontWeight.Light,
            color = colorScheme.onSurface
        )},
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(
                    text = "OK",
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant
                )
            }
        },
        shape = newRoundedShape
    )
}