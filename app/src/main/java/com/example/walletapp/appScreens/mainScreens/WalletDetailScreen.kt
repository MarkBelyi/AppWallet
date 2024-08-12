package com.example.walletapp.appScreens.mainScreens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(wallet: Wallets, viewModel: appViewModel, onBack: () -> Unit) {
    val signers by viewModel.allSigners.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val (isHidden, setIsHidden) = remember { mutableStateOf(wallet.myFlags.isNotEmpty() && wallet.myFlags.first() == '1') }
    var isLoading by remember { mutableStateOf(false) }
    var showHiddenDialog by remember { mutableStateOf(false) }

    // Показ диалогового окна для подтверждения скрытия кошелька
    if (showHiddenDialog) {
        HiddenItemAlertDialog(isHidden = isHidden, onDismiss = { confirm ->
            showHiddenDialog = false
            if (confirm) {
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
        })
    }

    BackHandler(onBack = onBack)
    
    Scaffold(
        containerColor = colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxSize()
                    ){
                        if(wallet.network in listOf(5010, 1010, 3040)){
                            Card(
                                shape = newRoundedShape,
                                border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.surface,
                                    contentColor = colorScheme.onSurface
                                )
                            ) {
                                Text(
                                    text = "TEST",
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = wallet.info,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
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
                            showHiddenDialog = true
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

                Text(text = stringResource(id = R.string.wallet_address), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = wallet.addr,
                    onValueChange = {},
                    textStyle = TextStyle(color = colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = newRoundedShape,
                    readOnly = true,
                    singleLine = true,
                    maxLines = 1,
                    leadingIcon = {
                        IconButton(onClick = {
                            if(wallet.network in listOf(5010, 1010, 3040)){
                                val urlTest = "https://nile.tronscan.org/#/address/${wallet.addr}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlTest))
                                context.startActivity(intent)
                            }else{
                                val url = "https://tronscan.org/#/address/${wallet.addr}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }) {
                            Icon(painter = painterResource(id = R.drawable.travel), contentDescription = "Travel", tint = colorScheme.onSurface)
                        }
                    },
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

                Text(text = stringResource(id = R.string.signers_), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                AddressList(slist = wallet.slist, signers = signers)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(id = R.string.balance_), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.surface, shape = newRoundedShape)
                        .padding(8.dp),
                ){
                    Text(
                        text = wallet.tokenShortNames.split(';').joinToString("\n"),
                        modifier = Modifier
                            .padding(8.dp),
                        fontSize = 14.sp,
                        color = colorScheme.onSurface
                    )
                }
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
            .background(color = colorScheme.surface, shape = newRoundedShape)
            .padding(12.dp),
    ) {
        addresses.forEachIndexed { index, address ->
            Text(
                text = "${index + 1}. $address",
                fontSize = 14.sp,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun HiddenItemAlertDialog(isHidden: Boolean, onDismiss: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        confirmButton = {
            Text(
                text = stringResource(id = R.string.yes),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface,
                modifier = Modifier
                    .clickable { onDismiss(true) }
                    .padding(16.dp)
            )
        },
        dismissButton = {
            Text(
                text = stringResource(id = R.string.no),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface,
                modifier = Modifier
                    .clickable { onDismiss(false) }
                    .padding(16.dp)
            )
        },
        title = {
            Text(
                text = if (isHidden)
                    stringResource(id = R.string.show_wallet)
                else stringResource(id = R.string.hidden_wallet),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = if (isHidden) stringResource(id = R.string.do_you_not_hide) else stringResource(id = R.string.do_you_hide),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            )
        },
        containerColor = colorScheme.surface,
        textContentColor = colorScheme.onSurface,
        titleContentColor = colorScheme.onSurface,
        shape = newRoundedShape,
        tonalElevation = 0.dp,
    )
}

