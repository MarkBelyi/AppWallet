package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Element.ClickableText
import com.example.walletapp.PullToRefreshLazyColumn.PullToRefreshWithCustomIndicator
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape


@Composable
fun Wallet(viewModel: appViewModel, onCreateClick: () -> Unit) {
    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val selectedWallet by viewModel.chooseWallet.observeAsState(initial = null)
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.filterWallets()
    }

    PullToRefreshWithCustomIndicator(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refreshWallets(context) {
                isRefreshing = false
            }
        },
        content = {
            if (selectedWallet == null) {
                WalletsList(wallets = wallets, onWalletClick = { wallet ->
                    viewModel.chooseWallet(wallet)
                }, onCreateClick = onCreateClick, viewModel = viewModel)
            } else {
                WalletDetailScreen(wallet = selectedWallet!!, viewModel = viewModel) {
                    viewModel.chooseWallet(null)
                }
            }
        }
    )
}


@Composable
fun WalletsList(wallets: List<Wallets>, onWalletClick: (Wallets) -> Unit, onCreateClick: () -> Unit, viewModel: appViewModel) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = colorScheme.inverseSurface)) {
        SearchBar(searchText, onTextChange = { newValue ->
            searchText = newValue
            viewModel.filterWalletsByName(newValue.text)
        }, viewModel = viewModel)

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
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
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ClickableText(text = stringResource(id = R.string.createWallet), onClick = onCreateClick)
                    }
                }
            } else {
                items(wallets) { wallet ->
                    WalletItem(wallet = wallet, onWalletClick = onWalletClick)
                }
            }
        }
    }
}



data class Blockchain(val id: Int, val name: String)

@Composable
fun SearchBar(searchText: TextFieldValue, onTextChange: (TextFieldValue) -> Unit, viewModel: appViewModel) {
    val showPopup = remember { mutableStateOf(false) }
    val showHidden by viewModel.showTestNetworks.observeAsState(initial = false)
    val blockchains = listOf(
        Blockchain(1000, "Bitcoin (BTC)"),
        Blockchain(3000, "Ethereum (ETH)"),
        Blockchain(5000, "Tron (TRX)")
    ).sortedBy { it.name }
    val selectedBlockchain by viewModel.selectedBlockchain.observeAsState(initial = null)
    val id = when (selectedBlockchain?.id) {
        1000 -> R.drawable.btc
        3000 -> R.drawable.eth
        5000 -> R.drawable.tron
        else -> R.drawable.history_fill1_wght400_grad0_opsz24
    }

    Column(
        modifier = Modifier.background(color = colorScheme.surface, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onSurface),
            singleLine = true,
            maxLines = 1,
            shape = newRoundedShape,
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = "search", tint = colorScheme.primary)
            },
            trailingIcon = {
                IconButton(onClick = { showPopup.value = true }) {
                    if (selectedBlockchain == null) {
                        Text("All", color = colorScheme.primary, fontWeight = FontWeight.Light)
                    } else {
                        Icon(painter = painterResource(id = id), contentDescription = "blockchain")
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                focusedLabelColor = colorScheme.primary,
                unfocusedContainerColor = colorScheme.surface,
                unfocusedLabelColor = colorScheme.onBackground,
                cursorColor = colorScheme.primary
            )
        )

        if (showPopup.value) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(x = -16, y = 200),
                onDismissRequest = { showPopup.value = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .background(colorScheme.surface, shape = newRoundedShape)
                        .border(width = 0.5.dp, color = colorScheme.primary, shape = newRoundedShape)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = showHidden,
                            onCheckedChange = {
                                viewModel.toggleShowHidden()
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colorScheme.surface,
                                uncheckedColor = colorScheme.primaryContainer,
                                checkmarkColor = colorScheme.primary
                            ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Скрытые", color = colorScheme.onSurface, modifier = Modifier.weight(1f), fontWeight = FontWeight.Light)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    LazyColumn {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.updateSelectedBlockchain(null)
                                        showPopup.value = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(painter = painterResource(id = R.drawable.wallet), contentDescription = null, tint = colorScheme.onSurface)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Все кошельки", color = colorScheme.onSurface, modifier = Modifier.weight(1f), fontWeight = FontWeight.Light)
                                if (selectedBlockchain == null) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(blockchains) { blockchain ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.updateSelectedBlockchain(blockchain)
                                        showPopup.value = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val iconId = when (blockchain.id) {
                                    1000 -> R.drawable.btc
                                    3000 -> R.drawable.eth
                                    5000 -> R.drawable.tron
                                    else -> R.drawable.history_fill1_wght400_grad0_opsz24
                                }
                                Icon(
                                    painter = painterResource(id = iconId),
                                    contentDescription = null,
                                    tint = colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = blockchain.name, color = colorScheme.onSurface, modifier = Modifier.weight(1f), fontWeight = FontWeight.Light)
                                if (selectedBlockchain == blockchain) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletItem(wallet: Wallets, onWalletClick: (Wallets) -> Unit) {
    val context = LocalContext.current
    val isAddressEmpty = wallet.addr.isEmpty()
    val network = wallet.network
    val iconResource = when (network) {
        1000, 1010 -> R.drawable.btc
        3000, 3040 -> R.drawable.eth
        5000, 5010 -> R.drawable.tron
        else -> R.drawable.wait
    }
    val isHidden = wallet.myFlags.startsWith("1")

    val tokensList = mutableListOf<String>()

    if (wallet.tokenShortNames.isNotBlank()) {

        wallet.tokenShortNames.split(";").filter { it.isNotBlank() }.forEach { token ->
            val parts = token.split(" ")
            val tokenName = parts.getOrNull(1) ?: ""
            tokensList.add(tokenName)
        }

    }



    Card(
        border = BorderStroke(width = 0.5.dp, color =  if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.primary),
        onClick = {
            if (!isAddressEmpty){
                onWalletClick(wallet)
            }
        },
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = "Blockchain network logo",
                    tint = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.primary,
                    modifier = Modifier.scale(1.2f)
                )

            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = wallet.info.uppercase(),
                    color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

                if (isAddressEmpty) {
                    Text(
                        context.getString(R.string.pending_wallet),
                        fontWeight = FontWeight.Light,
                        color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                } else {
                    Spacer(Modifier.height(4.dp))

                    Row (modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        val displayTokens = if (tokensList.size > 3) tokensList.take(3) else tokensList
                        displayTokens.forEach { token ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .border(
                                        width = 0.5.dp,
                                        color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = token,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface
                                )
                            }
                        }
                        if (tokensList.size > 3) {
                            Text(
                                text = "+${tokensList.size - 3}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp)
            ) {
                if (!isHidden) {
                    Text(
                        text = wallet.tokenShortNames.split(";")
                            .find { it.contains("TRX") || it.contains("BTC") || it.contains("ETH") }
                            ?: "",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = "Скрыт",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}





