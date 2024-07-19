package com.example.walletapp.appScreens.mainScreens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WalletDetailScreen(wallet: Wallets, viewModel: appViewModel, onBack: () -> Unit) {
    val signers by viewModel.allSigners.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val (isHidden, setIsHidden) = remember { mutableStateOf(wallet.myFlags.first() == '1') }
    var isLoading by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)
    
    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = wallet.info, color = colorScheme.onSurface, fontWeight = FontWeight.Light) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.scale(1.2f),
                            tint = colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isLoading = true
                            val newFlags = if (isHidden) {
                                '0' + wallet.myFlags.substring(1)
                            } else {
                                '1' + wallet.myFlags.substring(1)
                            }
                            viewModel.updateWalletFlags(wallet.myUNID, newFlags) {
                                isLoading = false
                                setIsHidden(!isHidden)
                            }
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = colorScheme.onSurface)
                        } else {
                            Icon(
                                painter = if (isHidden) painterResource(id = R.drawable.ic_baseline_visibility_off_24) else painterResource(id = R.drawable.ic_baseline_visibility_24),
                                contentDescription = "Toggle Visibility",
                                tint = colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        ) {
            items(1) {
                Text(text = "Адрес кошелька:", fontSize = 16.sp, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

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
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("address", wallet.addr)
                            clipboardManager.setPrimaryClip(clip)
                            Toast.makeText(context, R.string.copytobuffer, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(painter = painterResource(id = R.drawable.copy), contentDescription = "Copy", tint = colorScheme.onSurface)
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Подписанты:", fontSize = 16.sp, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                AddressList(slist = wallet.slist, signers = signers)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Баланс:", fontSize = 16.sp, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = wallet.tokenShortNames.split(';').joinToString("\n"),
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

@Composable
fun AddressList(slist: String, signers: List<Signer>) {
    val slistJson = JSONObject(slist)
    val addresses = mutableListOf<String>()

    val keys = slistJson.keys()

    while (keys.hasNext()) {
        val key = keys.next()
        val addressObject = slistJson.optJSONObject(key)
        if (addressObject != null && addressObject.has("ecaddress")) {
            val ecAddress = addressObject.optString("ecaddress")
            if (ecAddress.isNotEmpty()) {
                val signer = signers.find { it.address == ecAddress }
                addresses.add(signer?.name ?: ecAddress)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorScheme.surface, shape = roundedShape)
            .padding(8.dp),
    ) {
        addresses.forEachIndexed { index, address ->
            Text(
                text = "${index + 1}. $address",
                fontSize = 14.sp,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/*
@Composable
fun HiddenItemAlertDialog(isHidden: Boolean, flags: String, onDismiss: () -> Unit, ){
    AlertDialog(
        onDismissRequest = {
            onDismiss() },
        confirmButton = {
            Text(text = "Yes", fontSize = 12.sp, fontWeight = FontWeight.Light, color = colorScheme.onSurface)
        },
        dismissButton = {
            Text(text = "No", fontSize = 12.sp, fontWeight = FontWeight.Light, color = colorScheme.onSurface)
        },
        title = {
            Text(text = "Скрытый кошелек", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
        },
        text = {
            Text(text = "Вы действительно хотите скрыть этот кошелек?", fontSize = 14.sp, fontWeight = FontWeight.Light, color = colorScheme.onSurface)
        },
        containerColor = colorScheme.surface,
        textContentColor = colorScheme.onSurface,
        titleContentColor = colorScheme.onSurface,
        shape = newRoundedShape,
        tonalElevation = 0.dp,
        modifier = Modifier.border(width = 0.5.dp, color = colorScheme.primary, shape = newRoundedShape)

    )
}
*/

