package com.example.walletapp.appScreens.mainScreens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.QR.generateQRCode
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun ShareAddress(){
    val context = LocalContext.current
    val outputText = GetMyAddr(context = context)
    val qrImage = generateQRCode(outputText)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(16.dp)
    ){

        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = stringResource(R.string.this_is_ypkey),
            color = colorScheme.onBackground,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(0.1f))

        Image(
            bitmap = qrImage,
            contentDescription = "QR Code",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(color = colorScheme.background)
                .aspectRatio(1f)
                .size(100.dp)
        )


        Spacer(modifier = Modifier.weight(0.1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = outputText,
                onValueChange = {},
                textStyle = TextStyle(color = colorScheme.onBackground),
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
                        Icon(painter = painterResource(id = R.drawable.copy), contentDescription = "Share", tint = colorScheme.onBackground)
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
                        Icon(painter = painterResource(id = R.drawable.share), contentDescription = "Copy", tint = colorScheme.onBackground)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedLeadingIconColor = colorScheme.onBackground,
                    focusedTrailingIconColor = colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.weight(0.4f))

    }
}