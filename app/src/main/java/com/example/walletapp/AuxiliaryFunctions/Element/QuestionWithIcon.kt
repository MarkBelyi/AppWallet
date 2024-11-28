package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun QuestionWithIcon(
) {
    val questionText = stringResource(id = R.string.why_i_need_this)
    var showDialog by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.text.ClickableText(
            text = AnnotatedString(questionText),
            onClick = { showDialog = true },
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = colorScheme.onSurface
            ),
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
        QuestionDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun QuestionDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = colorScheme.surface,
        title = {
            Text(
                text = stringResource(id = R.string.my_public_key),
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = stringResource(R.string.this_is_ypkey),
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            )
        },
        shape = newRoundedShape,
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                shape = newRoundedShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("OK")
            }
        }
    )
}