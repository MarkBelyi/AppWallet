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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.QR.generateQRCode
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

object RecieveRoute {
    const val RECEIVE = "receive"
    const val SHARE_ADDRESS = "share_address/{walletAddr}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveScreen(viewModel: appViewModel, onCreateClick: () -> Unit, onBackClick: () -> Unit) {
    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())
    val navController = rememberNavController()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Receive") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { paddingValues ->
        NavHost(navController, startDestination = RecieveRoute.RECEIVE) {
            composable(RecieveRoute.RECEIVE) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorScheme.inverseSurface)
                    .padding(paddingValues),
                ) {
                    SearchBar(searchText, onTextChange = { newValue ->
                        searchText = newValue
                        viewModel.filterWalletsByName(newValue.text)
                    }, viewModel = viewModel)

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    ) {
                        if (wallets.isEmpty()) {
                            items(1) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.no_wallets),
                                        color = colorScheme.onSurface,
                                        modifier = Modifier.fillMaxSize(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ClickableText(
                                        text = AnnotatedString(stringResource(id = R.string.createWallet)),
                                        onClick = { onCreateClick() },
                                        style = TextStyle(color = colorScheme.primary)
                                    )
                                }
                            }
                        } else {
                            items(wallets) { wallet ->
                                WalletItem(wallet = wallet, onWalletClick = {
                                    navController.navigate(RecieveRoute.SHARE_ADDRESS.replace("{walletAddr}", wallet.addr))
                                })
                            }
                        }
                    }
                }
            }
            composable(RecieveRoute.SHARE_ADDRESS) { backStackEntry ->
                backStackEntry.arguments?.getString("walletAddr")?.let { walletAddr ->
                    ShareAddressScreen(walletAddr, onBackClick)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAddressScreen(walletAddr: String, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val qrImage = generateQRCode(walletAddr)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Share address", color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = colorScheme.surface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            QuestionWithIcon()

            Spacer(modifier = Modifier.weight(0.05f))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .clip(roundedShape)
                    .background(Color.White)
                    .border(1.dp, colorScheme.onSurface, roundedShape)
            ) {
                Image(
                    bitmap = qrImage,
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = walletAddr,
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
                            val clip = ClipData.newPlainText("address", walletAddr)
                            clipboardManager.setPrimaryClip(clip)
                            Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(painter = painterResource(id = R.drawable.copy), contentDescription = "Copy", tint = colorScheme.primary)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, walletAddr)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }) {
                            Icon(painter = painterResource(id = R.drawable.share), contentDescription = "Share", tint = colorScheme.primary)
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