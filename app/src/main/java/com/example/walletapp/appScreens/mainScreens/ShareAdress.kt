package com.example.walletapp.appScreens.mainScreens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.walletapp.QR.generateQRCode
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.ui.theme.roundedShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAddress(
    onBackClick: () -> Unit
){
    val context = LocalContext.current
    val outputText = GetMyAddr(context = context)
    val qrImage = generateQRCode(outputText)

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Share address", color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.Start
        ){

        Spacer(modifier = Modifier.weight(0.3f))

        QuestionWithIcon()

        Spacer(modifier = Modifier.weight(0.05f))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .clip(roundedShape)
                .background(Color.White.copy(alpha = 0.8f))
                .border(0.75.dp, colorScheme.onSurface, roundedShape)
        ) {
            Image(
                bitmap = qrImage,
                contentDescription = "QR Code",
                modifier = Modifier
                    .aspectRatio(1f),
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = outputText,
                onValueChange = {},
                textStyle = TextStyle(color = colorScheme.onSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = roundedShape,
                readOnly = true,
                singleLine = true,
                maxLines = 1,
                leadingIcon = {
                    IconButton(onClick = {
                        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("address", outputText)
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, R.string.copytobuffer, Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.copy), contentDescription = "Share", tint = colorScheme.primary)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        // Открытие окна "Поделиться"
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, outputText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.share), contentDescription = "Copy", tint = colorScheme.primary)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedLeadingIconColor = colorScheme.onSurface,
                    focusedTrailingIconColor = colorScheme.onSurface
                )
            )
        }

        Spacer(modifier = Modifier.weight(0.4f))
        }
    }
}

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
        ClickableText(
            text = AnnotatedString(questionText),
            onClick = {showDialog = true},
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
fun QuestionDialog(onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss()},
        title = { Text(text = stringResource(id = R.string.my_public_key))},
        text = { Text(text = stringResource(R.string.this_is_ypkey),)},
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