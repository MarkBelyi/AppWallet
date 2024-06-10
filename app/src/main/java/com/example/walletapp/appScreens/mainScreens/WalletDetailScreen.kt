package com.example.walletapp.appScreens.mainScreens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R
import com.example.walletapp.ui.theme.roundedShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(wallet: Wallets, onBack: () -> Unit) {
    val context = LocalContext.current
    BackHandler(onBack = onBack)
    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = wallet.info, color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onBack() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.scale(1.2f),
                            tint = colorScheme.primary
                        )
                    }
                }
            )
        }
    ) {padding ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            items(1){
                Text(text = "Адрес кошелька:", fontSize = 16.sp, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    border = BorderStroke(width = 0.75.dp, color = colorScheme.primary),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = wallet.addr,
                        onValueChange = {},
                        textStyle = TextStyle(color = colorScheme.onSurface),
                        modifier = Modifier.fillMaxWidth(),
                        shape = roundedShape,
                        readOnly = true,
                        singleLine = true,
                        maxLines = 1,
                        trailingIcon = {
                            IconButton(onClick = {
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("address", wallet.addr)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(context, R.string.copytobuffer, Toast.LENGTH_SHORT)
                                    .show()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.copy),
                                    contentDescription = "Share",
                                    tint = colorScheme.primary
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedLeadingIconColor = colorScheme.onSurface,
                            focusedTrailingIconColor = colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Подписанты:", fontSize = 16.sp, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    border = BorderStroke(width = 1.dp, color = colorScheme.primary),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = wallet.slist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = colorScheme.surface, shape = roundedShape)
                            .padding(8.dp),
                        fontSize = 14.sp,
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Баланс:", fontSize = 16.sp, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    border = BorderStroke(width = 1.dp, color = colorScheme.primary),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = wallet.tokenShortNames,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = colorScheme.surface, shape = roundedShape)
                            .padding(8.dp),
                        fontSize = 14.sp,
                        color = colorScheme.onSurface
                    )
                }
            }

        }

    }

}